package io.cloudflight.jems.server.project.service.application.submit_application

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

class ProjectNotificationEventListenersTest: UnitTest() {

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
    lateinit var listeners: ProjectNotificationEventListeners

    @BeforeEach
    internal fun reset() {
        clearAllMocks()
    }

    @Test
    fun sendNotifications() {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject)) } answers { }

        listeners.sendNotifications(
            ProjectNotificationEvent(mockk(), summary(mockk()), ApplicationStatus.SUBMITTED)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(NotificationType.ProjectSubmitted, any()) }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
    }

    @Test
    fun storeAudit() {
        val slotAudit = slot<JemsAuditEvent>()
        every { eventPublisher.publishEvent(capture(slotAudit)) } answers { }

        val applicationStatus = ApplicationStatus.CONDITIONS_SUBMITTED
        val applicationEvent = ProjectNotificationEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS),
            newStatus = applicationStatus,
        )

        listeners.storeAudit(applicationEvent)

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
