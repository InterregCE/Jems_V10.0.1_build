package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.event.JemsAsyncMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.service.model.UserEmailNotification
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.util.UUID

class GlobalProjectNotificationServiceTest : UnitTest() {

    companion object {
        private const val CALL_ID = 1L
        private const val PROJECT_ID = 5L

        val step1submittedToAll = ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmittedStep1,
            active = true,
            sendToManager = true,
            sendToLeadPartner = true,
            sendToProjectPartners = true,
            sendToProjectAssigned = true,
            sendToControllers = false,
            emailSubject = "Application Step 1 Submitted",
            emailBody = "test step 1"
        )

        val submittedNone = ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmitted,
            active = true,
            sendToManager = false,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = false,
            emailSubject = "Application Submitted",
            emailBody = "test"
        )

        val partnerReportSubmitted = ProjectNotificationConfiguration(
            id = NotificationType.PartnerReportSubmitted,
            active = true,
            sendToManager = false,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = true,
            emailSubject = "PartnerReport Submitted",
            emailBody = "test"
        )

        val projectReportSubmitted = ProjectNotificationConfiguration(
            id = NotificationType.ProjectReportSubmitted,
            active = true,
            sendToManager = false,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = true,
            emailSubject = "ProjectReport Submitted",
            emailBody = "test"
        )

    }

    @MockK
    private lateinit var projectNotificationRecipientServiceInteractor: ProjectNotificationRecipientServiceInteractor

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var callPersistence: CallPersistence

    @MockK
    private lateinit var callNotificationConfigPersistence: CallNotificationConfigurationsPersistence

    @MockK
    private lateinit var notificationPersistence: NotificationPersistence

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var programmeDataPersistence: ProgrammeDataPersistence

    @InjectMockKs
    private lateinit var service: GlobalProjectNotificationService

    @BeforeEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun `sendNotifications - all enabled`() {
        val notifType = NotificationType.ProjectSubmittedStep1

        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every {
            callNotificationConfigPersistence.getActiveNotificationOfType(
                CALL_ID,
                notifType
            )
        } returns step1submittedToAll
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForProjectManagersAndAssignedUsers(
                step1submittedToAll,
                PROJECT_ID
            )
        } returns mapOf(
            "manager.inactive@jems.eu" to UserEmailNotification(true, UserStatus.INACTIVE),
        )
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForPartners(
                step1submittedToAll,
                PROJECT_ID
            )
        } returns mapOf(
            "lead-partner@jems.eu" to UserEmailNotification(true, UserStatus.ACTIVE),
            "partner.no-email@jems.eu" to UserEmailNotification(false, UserStatus.ACTIVE),
        )
        every { callPersistence.getCallSummaryById(CALL_ID) } returns mockk {
            every { id } returns CALL_ID
            every { name } returns "call-name"
        }
        every { programmeDataPersistence.getProgrammeName() } returns "programme-name"

        val slotNotification = slot<NotificationInApp>()
        every { notificationPersistence.saveNotification(capture(slotNotification)) } answers { }
        val slotEmail = slot<JemsAsyncMailEvent>()
        every { eventPublisher.publishEvent(capture(slotEmail)) } answers { }

        val variables = mapOf(
            NotificationVariable.ProjectId to PROJECT_ID,
            NotificationVariable.ProjectIdentifier to "P005",
            NotificationVariable.ProjectAcronym to "5 acr",
        )
        service.sendNotifications(notifType, variables)

        val templateVariables = variables.plus(
            mapOf(
                NotificationVariable.CallId to CALL_ID,
                NotificationVariable.CallName to "call-name",
                NotificationVariable.ProgrammeName to "programme-name",
            )
        ).mapKeys { it.key.variable }
        assertThat(slotNotification.captured).isEqualTo(
            NotificationInApp(
                subject = "Application Step 1 Submitted",
                body = "test step 1",
                type = NotificationType.ProjectSubmittedStep1,
                time = slotNotification.captured.time,
                templateVariables = templateVariables,
                recipientsInApp = setOf("lead-partner@jems.eu", "partner.no-email@jems.eu", "manager.inactive@jems.eu"),
                recipientsEmail = setOf("lead-partner@jems.eu"),
                emailTemplate = "notification.html",
                groupId = slotNotification.captured.groupId,
            )
        )
        assertThat(slotEmail.captured).isEqualTo(
            JemsAsyncMailEvent(
                emailTemplateFileName = "notification.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "Application Step 1 Submitted",
                    templateVariables = templateVariables.plus("body" to "test step 1")
                        .map { Variable(it.key, it.value) }.toSet(),
                    recipients = setOf("lead-partner@jems.eu"),
                    messageType = "ProjectSubmittedStep1",
                ),
            )
        )
    }

    @Test
    fun `sendNotifications - all disabled`() {
        val notifType = NotificationType.ProjectSubmitted

        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every {
            callNotificationConfigPersistence.getActiveNotificationOfType(
                CALL_ID,
                notifType
            )
        } returns submittedNone
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForProjectManagersAndAssignedUsers(
                submittedNone,
                PROJECT_ID
            )
        } returns emptyMap()
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForPartners(
                submittedNone,
                PROJECT_ID
            )
        } returns emptyMap()
        every { callPersistence.getCallSummaryById(CALL_ID) } returns mockk {
            every { id } returns CALL_ID
            every { name } returns "call-name"
        }
        every { programmeDataPersistence.getProgrammeName() } returns "programme-name"

        val slotNotification = slot<NotificationInApp>()
        every { notificationPersistence.saveNotification(capture(slotNotification)) } answers { }
        val slotEmail = slot<JemsAsyncMailEvent>()
        every { eventPublisher.publishEvent(capture(slotEmail)) } answers { }

        val variables = mapOf(
            NotificationVariable.ProjectId to PROJECT_ID,
            NotificationVariable.ProjectIdentifier to "P005",
            NotificationVariable.ProjectAcronym to "5 acr",
        )
        service.sendNotifications(notifType, variables)

        val templateVariables = variables.plus(
            mapOf(
                NotificationVariable.CallId to CALL_ID,
                NotificationVariable.CallName to "call-name",
                NotificationVariable.ProgrammeName to "programme-name",
            )
        ).mapKeys { it.key.variable }
        assertThat(slotNotification.captured).isEqualTo(
            NotificationInApp(
                subject = "Application Submitted",
                body = "test",
                type = NotificationType.ProjectSubmitted,
                time = slotNotification.captured.time,
                templateVariables = templateVariables,
                recipientsInApp = emptySet(),
                recipientsEmail = emptySet(),
                emailTemplate = "notification.html",
                groupId = slotNotification.captured.groupId,
            )
        )
        assertThat(slotEmail.captured).isEqualTo(
            JemsAsyncMailEvent(
                emailTemplateFileName = "notification.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "Application Submitted",
                    templateVariables = templateVariables.plus("body" to "test").map { Variable(it.key, it.value) }
                        .toSet(),
                    recipients = emptySet(),
                    messageType = "ProjectSubmitted",
                ),
            )
        )
    }

    @Test
    fun `sendNotifications - partnerReport`() {
        val notificationType = NotificationType.PartnerReportSubmitted

        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every {
            callNotificationConfigPersistence.getActiveNotificationOfType(
                CALL_ID,
                notificationType
            )
        } returns partnerReportSubmitted
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForProjectManagersAndAssignedUsers(
                partnerReportSubmitted, PROJECT_ID
            )
        } returns emptyMap()
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForSpecificPartner(
                partnerReportSubmitted,
                PROJECT_ID,
                91L
            )
        } returns mapOf(
            "lead-partner@jems.eu" to UserEmailNotification(true, UserStatus.ACTIVE),
        )
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForPartnerControllers(
                partnerReportSubmitted,
                91L
            )
        } returns mapOf(
            "controller@jems.eu" to UserEmailNotification(true, UserStatus.ACTIVE),
            "controller.inactive@jems.eu" to UserEmailNotification(true, UserStatus.INACTIVE),
            "controller.no-email@jems.eu" to UserEmailNotification(false, UserStatus.ACTIVE),
        )
        every { callPersistence.getCallSummaryById(CALL_ID) } returns mockk {
            every { id } returns CALL_ID
            every { name } returns "call-name"
        }
        every { programmeDataPersistence.getProgrammeName() } returns "programme-name"

        val slotNotification = slot<NotificationInApp>()
        every { notificationPersistence.saveNotification(capture(slotNotification)) } answers { }
        val slotEmail = slot<JemsAsyncMailEvent>()
        every { eventPublisher.publishEvent(capture(slotEmail)) } answers { }

        val variables = mapOf(
            NotificationVariable.ProjectId to PROJECT_ID,
            NotificationVariable.ProjectIdentifier to "P005",
            NotificationVariable.ProjectAcronym to "5 acr",
            NotificationVariable.PartnerId to 91L,
            NotificationVariable.PartnerRole to ProjectPartnerRole.LEAD_PARTNER,
            NotificationVariable.PartnerNumber to 1,
            NotificationVariable.PartnerAbbreviation to "LP-1",
            NotificationVariable.PartnerReportId to 92L,
            NotificationVariable.PartnerReportNumber to 1,
        )
        service.sendNotifications(notificationType, variables)

        val templateVariables = variables.plus(
            mapOf(
                NotificationVariable.CallId to CALL_ID,
                NotificationVariable.CallName to "call-name",
                NotificationVariable.ProgrammeName to "programme-name",
            )
        ).mapKeys { it.key.variable }
        assertThat(slotNotification.captured).isEqualTo(
            NotificationInApp(
                subject = "PartnerReport Submitted",
                body = "test",
                type = NotificationType.PartnerReportSubmitted,
                time = slotNotification.captured.time,
                templateVariables = templateVariables,
                recipientsInApp = setOf(
                    "lead-partner@jems.eu",
                    "controller@jems.eu",
                    "controller.inactive@jems.eu",
                    "controller.no-email@jems.eu"
                ),
                recipientsEmail = setOf("lead-partner@jems.eu", "controller@jems.eu"),
                emailTemplate = "notification.html",
                groupId = slotNotification.captured.groupId,
            )
        )
        assertThat(slotEmail.captured).isEqualTo(
            JemsAsyncMailEvent(
                emailTemplateFileName = "notification.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "PartnerReport Submitted",
                    templateVariables = templateVariables.plus("body" to "test").map { Variable(it.key, it.value) }
                        .toSet(),
                    recipients = setOf("lead-partner@jems.eu", "controller@jems.eu"),
                    messageType = "PartnerReportSubmitted",
                ),
            )
        )
    }

    @Test
    fun `sendNotifications - projectReport`() {
        val notificationType = NotificationType.ProjectReportSubmitted

        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every {
            callNotificationConfigPersistence.getActiveNotificationOfType(
                CALL_ID,
                notificationType
            )
        } returns projectReportSubmitted
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForProjectManagersAndAssignedUsers(
                projectReportSubmitted, PROJECT_ID
            )
        } returns emptyMap()
        every {
            projectNotificationRecipientServiceInteractor.getEmailsForPartners(
                projectReportSubmitted,
                PROJECT_ID
            )
        } returns mapOf(
            "lead-partner@jems.eu" to UserEmailNotification(true, UserStatus.ACTIVE),
            "pp@jems.eu" to UserEmailNotification(true, UserStatus.ACTIVE)
        )
        every { callPersistence.getCallSummaryById(CALL_ID) } returns mockk {
            every { id } returns CALL_ID
            every { name } returns "call-name"
        }
        every { programmeDataPersistence.getProgrammeName() } returns "programme-name"

        val slotNotification = slot<NotificationInApp>()
        every { notificationPersistence.saveNotification(capture(slotNotification)) } answers { }
        val slotEmail = slot<JemsAsyncMailEvent>()
        every { eventPublisher.publishEvent(capture(slotEmail)) } answers { }

        val variables = mapOf(
            NotificationVariable.ProjectId to PROJECT_ID,
            NotificationVariable.ProjectIdentifier to "P005",
            NotificationVariable.ProjectAcronym to "5 acr",
            NotificationVariable.ProjectReportId to 91L,
            NotificationVariable.ProjectReportNumber to 1,
        )
        service.sendNotifications(notificationType, variables)

        val templateVariables = variables.plus(
            mapOf(
                NotificationVariable.CallId to CALL_ID,
                NotificationVariable.CallName to "call-name",
                NotificationVariable.ProgrammeName to "programme-name",
            )
        ).mapKeys { it.key.variable }
        assertThat(slotNotification.captured).isEqualTo(
            NotificationInApp(
                subject = "ProjectReport Submitted",
                body = "test",
                type = NotificationType.ProjectReportSubmitted,
                time = slotNotification.captured.time,
                templateVariables = templateVariables,
                recipientsInApp = setOf("lead-partner@jems.eu", "pp@jems.eu"),
                recipientsEmail = setOf("lead-partner@jems.eu", "pp@jems.eu"),
                emailTemplate = "notification.html",
                groupId = slotNotification.captured.groupId,
            )
        )
        assertThat(slotEmail.captured).isEqualTo(
            JemsAsyncMailEvent(
                emailTemplateFileName = "notification.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "ProjectReport Submitted",
                    templateVariables = templateVariables.plus("body" to "test").map { Variable(it.key, it.value) }
                        .toSet(),
                    recipients = setOf("lead-partner@jems.eu", "pp@jems.eu"),
                    messageType = "ProjectReportSubmitted",
                ),
            )
        )
    }

    @Test
    fun sendSystemNotification() {
        every { projectNotificationRecipientServiceInteractor.getSystemAdminEmails() } returns
                mapOf("sys-admin-user" to mockk())
        val slotNotification = slot<NotificationInApp>()
        every { notificationPersistence.saveOrUpdateSystemNotification(capture(slotNotification)) } answers { }

        val uuid = UUID.randomUUID()
        service.sendSystemNotification("title", "body", uuid)

        assertThat(slotNotification.captured).isEqualTo(
            NotificationInApp(
                subject = "title",
                body = "body",
                type = NotificationType.SystemMessage,
                time = slotNotification.captured.time,
                templateVariables = emptyMap(),
                recipientsInApp = setOf("sys-admin-user"),
                recipientsEmail = emptySet(),
                emailTemplate = null,
                groupId = uuid,
            )
        )
    }

}
