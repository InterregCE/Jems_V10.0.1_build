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
import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.notification.inApp.service.model.Notification
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProject
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
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
            sendToLeadPartner = false,
            sendToProjectPartners = true,
            sendToProjectAssigned = true,
            emailSubject = "Application Step 1 Submitted",
            emailBody = "test step 1"
        )

        val submitted_notification_config = ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmitted,
            active = true,
            sendToManager = true,
            sendToLeadPartner = true,
            sendToProjectPartners = false,
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

        val programmeUserGlobal = UserSummary(
            id = 118L,
            email = "bruno.mars@dcshoes.com",
            name = "bruno",
            surname = "mars",
            userRole = UserRoleSummary(id = 1151L, name = "aaaadmin", isDefault = false),
            userStatus = UserStatus.ACTIVE,
        )

        private fun summary(status: ApplicationStatus) = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = CALL_ID,
            callName = "call",
            acronym = "project acronym",
            status = status,
        )
    }

    @RelaxedMockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var callNotificationConfigPersistence: CallNotificationConfigurationsPersistence

    @MockK
    private lateinit var userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    private lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence

    @MockK
    private lateinit var userProjectPersistence: UserProjectPersistence

    @MockK
    private lateinit var notificationPersistence: NotificationPersistence

    @MockK
    private lateinit var userPersistence: UserPersistence

    @MockK
    private lateinit var userRolePersistence: UserRolePersistence

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
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(leadPartner, partner)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(3L)) } returns setOf(partnerCollaborator)
        every { userProjectPersistence.getUsersForProject(PROJECT_ID) } returns setOf(programmeUser)
        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            UserRolePermission.getGlobalProjectRetrievePermissions(), emptySet()
        ) } returns setOf(1151L)
        every { userPersistence.findAllWithRoleIdIn(setOf(1151L)) } returns listOf(programmeUserGlobal)
        val slotNotification = slot<Notification>()
        val emails = slot<Set<String>>()
        every { notificationPersistence.saveNotifications(capture(slotNotification), capture(emails)) } returns Unit

        val applicationEvent = ProjectNotificationEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.STEP1_DRAFT),
            newStatus = applicationStatus
        )
        projectNotificationEventListeners.publishJemsMailEvent(applicationEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(capture(auditSlot)) }
        verify(exactly = 0) { userProjectCollaboratorPersistence.getUserIdsForProject(any()) }
        verify(exactly = 0) { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(2L)) }

        assertThat(auditSlot.captured).isEqualTo(
            JemsMailEvent(
                "notification.html",
                MailNotificationInfo(
                    subject = "Application Step 1 Submitted",
                    templateVariables =
                    setOf(
                        Variable("projectId", 5L),
                        Variable("projectIdentifier", "01"),
                        Variable("projectAcronym", "project acronym"),
                        Variable("body", "test step 1"),
                    ),
                    recipients = setOf("pp1.collaborator@jems.eu", "john.doe@ce.eu", "bruno.mars@dcshoes.com"),
                    messageType = notifType.name
                ),
            )
        )
        assertThat(slotNotification.captured).isEqualTo(
            Notification(
                subject = "Application Step 1 Submitted",
                body = "test step 1",
                type = notifType,
                time = slotNotification.captured.time,
                project = NotificationProject(CALL_ID, "call", projectId = 5L, projectIdentifier = "01", projectAcronym = "project acronym"),
            ),
        )
        assertThat(emails.captured).containsExactly("pp1.collaborator@jems.eu", "john.doe@ce.eu", "bruno.mars@dcshoes.com")
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
        val slotNotification = slot<Notification>()
        val emails = slot<Set<String>>()
        every { notificationPersistence.saveNotifications(capture(slotNotification), capture(emails)) } returns Unit

        val applicationEvent = ProjectNotificationEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.DRAFT),
            newStatus = applicationStatus
        )
        projectNotificationEventListeners.publishJemsMailEvent(applicationEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(capture(auditSlot)) }
        verify(exactly = 0) { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(3L)) }
        verify(exactly = 0) { userProjectPersistence.getUsersForProject(any()) }

        assertThat(auditSlot.captured).isEqualTo(
            JemsMailEvent(
                "notification.html",
                MailNotificationInfo(
                    subject = "Application Submitted",
                    templateVariables =
                    setOf(
                        Variable("projectId", 5L),
                        Variable("projectIdentifier", "01"),
                        Variable("projectAcronym", "project acronym"),
                        Variable("body", "test"),
                    ),
                    recipients = setOf("manager@jems.eu", "lp.collaborator@jems.eu"),
                    messageType = notifType.name
                ),
            )
        )
        assertThat(slotNotification.captured).isEqualTo(
            Notification(
                subject = "Application Submitted",
                body = "test",
                type = notifType,
                time = slotNotification.captured.time,
                project = NotificationProject(CALL_ID, "call", projectId = 5L, projectIdentifier = "01", projectAcronym = "project acronym"),
            ),
        )
        assertThat(emails.captured).containsExactly("manager@jems.eu", "lp.collaborator@jems.eu")
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
