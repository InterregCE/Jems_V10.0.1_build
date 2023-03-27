package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.event.JemsAsyncMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProjectBase
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class GlobalProjectNotificationServiceTest: UnitTest() {

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
            emailSubject = "Application Submitted",
            emailBody = "test"
        )

        val projectManager = CollaboratorAssignedToProject(
            userId = 21L,
            userEmail = "project.manager@jems.eu",
            level = ProjectCollaboratorLevel.MANAGE,
        )

        private val leadPartner = ProjectPartnerDetail(
            projectId = PROJECT_ID,
            id = 2L,
            active = true,
            abbreviation = "A",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = "A",
            nameInEnglish = "A",
            createdAt = ZonedDateTime.now().minusDays(5),
            sortNumber = 1,
            partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
            partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
            nace = NaceGroupLevel.A,
            otherIdentifierNumber = null,
            otherIdentifierDescription = emptySet(),
            pic = null,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            legalStatusId = null,
        )

        private val partner = ProjectPartnerDetail(
            projectId = PROJECT_ID,
            id = 3L,
            active = true,
            abbreviation = "B",
            role = ProjectPartnerRole.PARTNER,
            nameInOriginalLanguage = "B",
            nameInEnglish = "B",
            createdAt = ZonedDateTime.now().minusDays(4),
            sortNumber = 1,
            partnerType = ProjectTargetGroup.Egtc,
            partnerSubType = PartnerSubType.SMALL_ENTERPRISE,
            nace = NaceGroupLevel.A,
            otherIdentifierNumber = null,
            otherIdentifierDescription = emptySet(),
            pic = null,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            legalStatusId = null,
        )

        val leadPartnerCollaborator = PartnerCollaborator(
            userId = 41L,
            partnerId = 2L,
            userEmail = "lp.collaborator@jems.eu",
            level = PartnerCollaboratorLevel.EDIT,
            gdpr = false
        )

        val partnerCollaborator = PartnerCollaborator(
            userId = 43L,
            partnerId = 3L,
            userEmail = "pp.collaborator@jems.eu",
            level = PartnerCollaboratorLevel.EDIT,
            gdpr = false
        )

        val programmeUser = UserSummary(
            id = 101L,
            email = "programme@user",
            name = "john",
            surname = "doe",
            userRole = UserRoleSummary(id = 2, name = "programme user", isDefault = true),
            userStatus = UserStatus.ACTIVE
        )

        val programmeUserGlobal = UserSummary(
            id = 118L,
            email = "global.retrieve@programme.user",
            name = "bruno",
            surname = "mars",
            userRole = UserRoleSummary(id = 1151L, name = "aaaadmin", isDefault = false),
            userStatus = UserStatus.ACTIVE,
        )
    }

    @MockK private lateinit var projectPersistence: ProjectPersistence
    @MockK private lateinit var callNotificationConfigPersistence: CallNotificationConfigurationsPersistence
    @MockK private lateinit var userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence
    @MockK private lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence
    @MockK private lateinit var partnerPersistence: PartnerPersistence
    @MockK private lateinit var userProjectPersistence: UserProjectPersistence
    @MockK private lateinit var userPersistence: UserPersistence
    @MockK private lateinit var userRolePersistence: UserRolePersistence
    @MockK private lateinit var notificationPersistence: NotificationPersistence
    @MockK private lateinit var eventPublisher: ApplicationEventPublisher

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
        every { callNotificationConfigPersistence.getActiveNotificationOfType(CALL_ID, notifType) } returns step1submittedToAll

        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(projectManager)
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(leadPartner, partner)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(2L)) } returns setOf(leadPartnerCollaborator)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(3L)) } returns setOf(partnerCollaborator)
        every { userProjectPersistence.getUsersForProject(PROJECT_ID) } returns setOf(programmeUser)

        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            UserRolePermission.getGlobalProjectRetrievePermissions(), emptySet()
        ) } returns setOf(1151L)
        every { userPersistence.findAllWithRoleIdIn(setOf(1151L)) } returns listOf(programmeUserGlobal)

        val slotNotification = slot<NotificationInApp>()
        every { notificationPersistence.saveNotification(capture(slotNotification)) } answers { }
        val slotEmail = slot<JemsAsyncMailEvent>()
        every { eventPublisher.publishEvent(capture(slotEmail)) } answers { }

        val project = NotificationProjectBase(PROJECT_ID, "P005", "5 acr")
        service.sendNotifications(notifType, project)

        assertThat(slotNotification.captured).isEqualTo(
            NotificationInApp(
                subject = "Application Step 1 Submitted",
                body = "test step 1",
                type = NotificationType.ProjectSubmittedStep1,
                time = slotNotification.captured.time,
                templateVariables = mutableMapOf(
                    "projectId" to 5L,
                    "projectIdentifier" to "P005",
                    "projectAcronym" to "5 acr",
                    "subject" to "Application Step 1 Submitted",
                    "body" to "test step 1",
                ),
                recipientsInApp = setOf("project.manager@jems.eu", "lp.collaborator@jems.eu",
                    "pp.collaborator@jems.eu", "programme@user", "global.retrieve@programme.user"),
                recipientsEmail = setOf("project.manager@jems.eu", "lp.collaborator@jems.eu",
                    "pp.collaborator@jems.eu", "programme@user", "global.retrieve@programme.user"),
                emailTemplate = "notification.html",
            )
        )
        assertThat(slotEmail.captured).isEqualTo(
            JemsAsyncMailEvent(
                emailTemplateFileName = "notification.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "Application Step 1 Submitted",
                    templateVariables = setOf(
                        Variable("projectIdentifier", "P005"),
                        Variable("projectId", 5L),
                        Variable("projectAcronym", "5 acr"),
                        Variable("body", "test step 1"),
                        Variable("subject", "Application Step 1 Submitted"),
                    ),
                    recipients = setOf("project.manager@jems.eu", "lp.collaborator@jems.eu",
                        "pp.collaborator@jems.eu", "programme@user", "global.retrieve@programme.user"),
                    messageType = "ProjectSubmittedStep1",
                ),
            )
        )
    }

    @Test
    fun `sendNotifications - all disabled`() {
        val notifType = NotificationType.ProjectSubmitted

        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every { callNotificationConfigPersistence.getActiveNotificationOfType(CALL_ID, notifType) } returns submittedNone

        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(projectManager)
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(leadPartner, partner)

        val slotNotification = slot<NotificationInApp>()
        every { notificationPersistence.saveNotification(capture(slotNotification)) } answers { }
        val slotEmail = slot<JemsAsyncMailEvent>()
        every { eventPublisher.publishEvent(capture(slotEmail)) } answers { }

        val project = NotificationProjectBase(PROJECT_ID, "P005", "5 acr")
        service.sendNotifications(notifType, project, Variable("extraVar", "please, persist"))

        assertThat(slotNotification.captured).isEqualTo(
            NotificationInApp(
                subject = "Application Submitted",
                body = "test",
                type = NotificationType.ProjectSubmitted,
                time = slotNotification.captured.time,
                templateVariables = mutableMapOf(
                    "projectId" to 5L,
                    "projectIdentifier" to "P005",
                    "projectAcronym" to "5 acr",
                    "subject" to "Application Submitted",
                    "body" to "test",
                    "extraVar" to "please, persist",
                ),
                recipientsInApp = emptySet(),
                recipientsEmail = emptySet(),
                emailTemplate = "notification.html",
            )
        )
        assertThat(slotEmail.captured).isEqualTo(
            JemsAsyncMailEvent(
                emailTemplateFileName = "notification.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "Application Submitted",
                    templateVariables = setOf(
                        Variable("projectIdentifier", "P005"),
                        Variable("projectId", 5L),
                        Variable("projectAcronym", "5 acr"),
                        Variable("subject", "Application Submitted"),
                        Variable("body", "test"),
                        Variable("extraVar", "please, persist"),
                    ),
                    recipients = emptySet(),
                    messageType = "ProjectSubmitted",
                ),
            )
        )
    }
}
