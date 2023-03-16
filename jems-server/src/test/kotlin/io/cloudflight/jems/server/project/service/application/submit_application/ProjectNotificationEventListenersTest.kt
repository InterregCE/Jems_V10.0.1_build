package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.NotificationPersistence
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.notification.model.Notification
import io.cloudflight.jems.server.notification.model.NotificationProject
import io.cloudflight.jems.server.notification.model.NotificationType
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
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
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class ProjectNotificationEventListenersTest: UnitTest() {

    companion object {
        private const val CALL_ID = 1L
        private const val PROJECT_ID = 5L

        val step1_submitted_notification_config = ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmittedStep1,
            active = true,
            sendToManager = false,
            sendToLeadPartner = true,
            sendToProjectPartners = false,
            sendToProjectAssigned = true,
            emailSubject = "Application Step 1 Submitted",
            emailBody = "test step 1"
        )

        val submitted_notification_config = ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmitted,
            active = true,
            sendToManager = true,
            sendToLeadPartner = true,
            sendToProjectPartners = true,
            sendToProjectAssigned = false,
            emailSubject = "Application Submitted",
            emailBody = "test"
        )

        val projectManager = CollaboratorAssignedToProject(
            userId = 21L,
            userEmail = "manager@jems.eu",
            level = ProjectCollaboratorLevel.MANAGE
        )
        val projectCollaborator = CollaboratorAssignedToProject(
            userId = 22L,
            userEmail =  "manager@jems.eu",
            level = ProjectCollaboratorLevel.EDIT
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
            level = PartnerCollaboratorLevel.EDIT
        )

        val partnerCollaborator = PartnerCollaborator(
            userId = 43L,
            partnerId = 3L,
            userEmail = "pp1.collaborator@jems.eu",
            level = PartnerCollaboratorLevel.EDIT
        )

        val programmeUser = UserSummary(
            id = 101L,
            email = "john.doe@ce.eu",
            name = "john",
            surname = "doe",
            userRole = UserRoleSummary(id = 2, name = "programme user", isDefault = true),
            userStatus = UserStatus.ACTIVE
        )

        private fun summary(status: ApplicationStatus) = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = CALL_ID,
            callName = "",
            acronym = "project acronym",
            status = status,
        )
    }

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var callNotificationConfigPersistence: CallNotificationConfigurationsPersistence

    @RelaxedMockK
    lateinit var userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence

    @RelaxedMockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @RelaxedMockK
    lateinit var partnerPersistence: PartnerPersistence

    @RelaxedMockK
    lateinit var userProjectPersistence: UserProjectPersistence

    @RelaxedMockK
    lateinit var notificationPersistence: NotificationPersistence

    @InjectMockKs
    lateinit var projectNotificationEventListeners: ProjectNotificationEventListeners


    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `submitting step1 application should trigger mail event`() {
        val applicationStatus = ApplicationStatus.STEP1_SUBMITTED
        val notifType = NotificationType.ProjectSubmittedStep1
        val auditSlot = slot<JemsMailEvent>()

        every { callNotificationConfigPersistence.getActiveNotificationOfType(CALL_ID, notifType) } returns step1_submitted_notification_config
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(projectManager, projectCollaborator)
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(leadPartner, partner)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(2L)) } returns setOf(leadPartnerCollaborator)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(3L)) } returns setOf(partnerCollaborator)
        every { userProjectPersistence.getUsersForProject(PROJECT_ID) } returns setOf(programmeUser)
        val slotNotifications = slot<List<Notification>>()
        every { notificationPersistence.saveNotifications(capture(slotNotifications)) } returns Unit

        val applicationEvent = ProjectNotificationEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.STEP1_DRAFT),
            newStatus = applicationStatus
        )
        projectNotificationEventListeners.publishJemsMailEvent(applicationEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(capture(auditSlot)) }

        assertThat(auditSlot.captured).isEqualTo(
            JemsMailEvent(
                "notification-project.html",
                MailNotificationInfo(
                    subject = "Application Step 1 Submitted",
                    templateVariables =
                    setOf(
                        Variable("projectId", 5L),
                        Variable("projectIdentifier", "01"),
                        Variable("projectAcronym", "project acronym"),
                        Variable("body", "test step 1"),
                    ),
                    recipients = setOf("john.doe@ce.eu", "lp.collaborator@jems.eu"),
                    messageType = notifType.name
                ),
            )
        )
        assertThat(slotNotifications.captured).containsExactly(
            Notification(
                email = "lp.collaborator@jems.eu",
                subject = "Application Step 1 Submitted",
                body = "test step 1",
                type = notifType,
                project = NotificationProject(projectId = 5L, projectIdentifier = "01", projectAcronym = "project acronym"),
            ),
            Notification(
                email = "john.doe@ce.eu",
                subject = "Application Step 1 Submitted",
                body = "test step 1",
                type = notifType,
                project = NotificationProject(projectId = 5L, projectIdentifier = "01", projectAcronym = "project acronym"),
            ),
        )
    }


    @Test
    fun `submitting application should trigger mail event`() {
        val applicationStatus = ApplicationStatus.SUBMITTED
        val notifType = NotificationType.ProjectSubmitted
        val auditSlot = slot<JemsMailEvent>()

        every { callNotificationConfigPersistence.getActiveNotificationOfType(CALL_ID, notifType) } returns submitted_notification_config
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(projectManager, projectCollaborator)
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(leadPartner, partner)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(2L)) } returns setOf(leadPartnerCollaborator)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(3L)) } returns setOf(partnerCollaborator)
        every { userProjectPersistence.getUsersForProject(PROJECT_ID) } returns setOf(programmeUser)
        val slotNotifications = slot<List<Notification>>()
        every { notificationPersistence.saveNotifications(capture(slotNotifications)) } returns Unit

        val applicationEvent = ProjectNotificationEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.DRAFT),
            newStatus = applicationStatus
        )
        projectNotificationEventListeners.publishJemsMailEvent(applicationEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(capture(auditSlot)) }

        assertThat(auditSlot.captured).isEqualTo(
            JemsMailEvent(
                "notification-project.html",
                MailNotificationInfo(
                    subject = "Application Submitted",
                    templateVariables =
                    setOf(
                        Variable("projectId", 5L),
                        Variable("projectIdentifier", "01"),
                        Variable("projectAcronym", "project acronym"),
                        Variable("body", "test"),
                    ),
                    recipients = setOf("manager@jems.eu", "pp1.collaborator@jems.eu", "lp.collaborator@jems.eu"),
                    messageType = notifType.name
                ),
            )
        )
        assertThat(slotNotifications.captured).containsExactly(
            Notification(
                email = "manager@jems.eu",
                subject = "Application Submitted",
                body = "test",
                type = notifType,
                project = NotificationProject(projectId = 5L, projectIdentifier = "01", projectAcronym = "project acronym"),
            ),
            Notification(
                email = "lp.collaborator@jems.eu",
                subject = "Application Submitted",
                body = "test",
                type = notifType,
                project = NotificationProject(projectId = 5L, projectIdentifier = "01", projectAcronym = "project acronym"),
            ),
            Notification(
                email = "pp1.collaborator@jems.eu",
                subject = "Application Submitted",
                body = "test",
                type = notifType,
                project = NotificationProject(projectId = 5L, projectIdentifier = "01", projectAcronym = "project acronym"),
            ),
        )
    }

    @Test
    fun `submitting application should trigger an audit log`() {
        val slotAudit = slot<JemsAuditEvent>()
        val applicationStatus = ApplicationStatus.SUBMITTED

        val applicationEvent = ProjectNotificationEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.DRAFT),
            newStatus = applicationStatus
        )

        projectNotificationEventListeners.publishJemsAuditEvent(applicationEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = "5", customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from DRAFT to SUBMITTED"
            )
        )
    }


    @Test
    fun `submit from status RETURNED_TO_APPLICANT_FOR_CONDITIONS should trigger an audit log`() {
        val slotAudit = slot<JemsAuditEvent>()
        val applicationStatus = ApplicationStatus.CONDITIONS_SUBMITTED

        val applicationEvent = ProjectNotificationEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS),
            newStatus = applicationStatus
        )

        projectNotificationEventListeners.publishJemsAuditEvent(applicationEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from RETURNED_TO_APPLICANT_FOR_CONDITIONS to CONDITIONS_SUBMITTED"
            )
        )
    }
}
