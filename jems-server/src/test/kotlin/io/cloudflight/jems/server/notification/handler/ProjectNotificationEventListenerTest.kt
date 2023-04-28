package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProjectBase
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class ProjectNotificationEventListenerTest: UnitTest() {

    companion object {
        private const val CALL_ID = 1L
        private const val PROJECT_ID = 5L

        private fun summary(status: ApplicationStatus) = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = CALL_ID,
            callName = "call",
            acronym = "project acronym",
            status = status,
        )
    }

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var notificationProjectService: GlobalProjectNotificationServiceInteractor

    @InjectMockKs
    lateinit var listener: ProjectNotificationEventListener

    @BeforeEach
    internal fun reset() {
        clearAllMocks()
    }

    @Test
    fun `send ProjectSubmitted notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.DRAFT), ApplicationStatus.SUBMITTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectSubmitted, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectSubmittedStep1 notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.DRAFT), ApplicationStatus.STEP1_SUBMITTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectSubmittedStep1, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectApprovedStep1 notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.STEP1_SUBMITTED), ApplicationStatus.STEP1_APPROVED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectApprovedStep1, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectApprovedWithConditionsStep1 notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.STEP1_SUBMITTED), ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectApprovedWithConditionsStep1, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectIneligibleStep1 notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.STEP1_SUBMITTED), ApplicationStatus.STEP1_INELIGIBLE)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectIneligibleStep1, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectNotApprovedStep1 notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.STEP1_SUBMITTED), ApplicationStatus.STEP1_NOT_APPROVED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectNotApprovedStep1, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectApproved notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.ELIGIBLE), ApplicationStatus.APPROVED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectApproved, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectApprovedWithConditions notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.ELIGIBLE), ApplicationStatus.APPROVED_WITH_CONDITIONS)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectApprovedWithConditions, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectIneligible notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.ELIGIBLE), ApplicationStatus.INELIGIBLE)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectIneligible, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectNotApproved notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.ELIGIBLE), ApplicationStatus.NOT_APPROVED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectNotApproved, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectReturnedToApplicant notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.ELIGIBLE), ApplicationStatus.RETURNED_TO_APPLICANT)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectReturnedToApplicant, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectResubmitted notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.RETURNED_TO_APPLICANT), ApplicationStatus.SUBMITTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectResubmitted, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectReturnedForConditions notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.ELIGIBLE), ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectReturnedForConditions, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectConditionsSubmitted notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS), ApplicationStatus.CONDITIONS_SUBMITTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectConditionsSubmitted, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectContracted notification`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.APPROVED), ApplicationStatus.CONTRACTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectContracted, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectInModification notification when project is contracted already`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.CONTRACTED), ApplicationStatus.IN_MODIFICATION)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectInModification, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectInModification notification when project is not contracted yet`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.APPROVED), ApplicationStatus.MODIFICATION_PRECONTRACTING)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectInModification, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectModificationSubmitted notification when project is contracted`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.IN_MODIFICATION), ApplicationStatus.MODIFICATION_SUBMITTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectModificationSubmitted, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectModificationSubmitted notification when project not contracted`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.MODIFICATION_PRECONTRACTING), ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectModificationSubmitted, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectModificationApproved notification when project contracted`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.MODIFICATION_SUBMITTED), ApplicationStatus.CONTRACTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectModificationApproved, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectModificationApproved notification when project not contracted`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED), ApplicationStatus.APPROVED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectModificationApproved, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectModificationRejected notification when project is contracted`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.MODIFICATION_SUBMITTED), ApplicationStatus.MODIFICATION_REJECTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectModificationRejected, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun `send ProjectModificationRejected notification when project not contracted`() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED), ApplicationStatus.MODIFICATION_REJECTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectModificationRejected, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun storeAudit() {
        val slotAudit = slot<JemsAuditEvent>()
        every { eventPublisher.publishEvent(capture(slotAudit)) } answers { }

        val applicationStatus = ApplicationStatus.CONDITIONS_SUBMITTED
        val applicationEvent = ProjectStatusChangeEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS),
            newStatus = applicationStatus,
        )

        listener.storeAudit(applicationEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(any<Any>()) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = "5", customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from RETURNED_TO_APPLICANT_FOR_CONDITIONS to CONDITIONS_SUBMITTED"
            )
        )
    }
}
