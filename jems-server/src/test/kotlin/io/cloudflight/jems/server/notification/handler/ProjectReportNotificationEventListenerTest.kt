package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.ZonedDateTime
import java.util.Map.entry

class ProjectReportNotificationEventListenerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 2L
        private const val PROJECT_REPORT_ID = 4L
        private const val PROJECT_REPORT_NUMBER = 7

        private fun projectReportSummary(status: ProjectReportStatus) = ProjectReportSubmissionSummary(
            id = PROJECT_REPORT_ID,
            reportNumber = PROJECT_REPORT_NUMBER,
            status = status,
            version = "1.0",
            firstSubmission = ZonedDateTime.now().minusDays(1),
            createdAt = ZonedDateTime.now(),

            projectId = PROJECT_ID,
            projectIdentifier = "01",
            projectAcronym = "project acronym",
        )
    }

    @MockK
    private lateinit var notificationProjectService: GlobalProjectNotificationServiceInteractor

    @InjectMockKs
    lateinit var listener: ProjectReportNotificationEventListener

    @BeforeEach
    internal fun reset() {
        clearAllMocks()
    }

    @ParameterizedTest(name = "send ProjectReport - {0} notification")
    @EnumSource(value = ProjectReportStatus::class, names = ["Submitted"], mode = EnumSource.Mode.INCLUDE)
    fun sendProjectReportNotification(projectReportStatus: ProjectReportStatus) {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val projectReportSummary = projectReportSummary(projectReportStatus)
        listener.sendNotifications(
            ProjectReportStatusChanged(mockk(), projectReportSummary)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(projectReportStatus.toNotificationType()!!, any()) }
        assertThat(slotVariable.captured).containsExactly(
            entry(NotificationVariable.ProjectId, PROJECT_ID),
            entry(NotificationVariable.ProjectIdentifier, "01"),
            entry(NotificationVariable.ProjectAcronym, "project acronym"),
            entry(NotificationVariable.ProjectReportId, PROJECT_REPORT_ID),
            entry(NotificationVariable.ProjectReportNumber, 7),
        )
    }
}
