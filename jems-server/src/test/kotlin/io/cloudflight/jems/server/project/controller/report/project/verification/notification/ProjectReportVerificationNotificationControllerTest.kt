package io.cloudflight.jems.server.project.controller.report.project.verification.notification

import io.cloudflight.jems.api.common.dto.file.UserSimpleDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.notification.ProjectReportVerificationNotificationDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import io.cloudflight.jems.server.project.service.report.project.verification.notification.getProjectReportVerificationNotification.GetProjectReportVerificationNotificationInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.notification.sendVerificationDoneByJsNotification.SendVerificationDoneByJsNotificationInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class ProjectReportVerificationNotificationControllerTest : UnitTest() {

    companion object {

        private const val PROJECT_ID = 10L
        private const val REPORT_ID = 11L
        private val createdAt = ZonedDateTime.now()

        val user = UserSimple(
            id = 1L, name = "test name",
            surname = "test surname",
            email = "test@email.com"
        )

        val expectedUser = UserSimpleDTO(
            id = 1L, name = "test name",
            surname = "test surname",
            email = "test@email.com"
        )

        val verificationNotification = ProjectReportVerificationNotification(
            id = 1L, reportId = REPORT_ID, triggeredByUser = user, createdAt = createdAt
        )

        val expectedVerificationNotification = ProjectReportVerificationNotificationDTO(
            reportId = REPORT_ID, triggeredByUser = expectedUser, createdAt = createdAt
        )
    }

    @MockK
    lateinit var getProjectReportVerificationNotificationInteractor: GetProjectReportVerificationNotificationInteractor

    @MockK
    lateinit var sendProjectReportVerificationNotificationInteractor: SendVerificationDoneByJsNotificationInteractor

    @InjectMockKs
    lateinit var projectReportVerificationNotificationController: ProjectReportVerificationNotificationController

    @Test
    fun getLastNotification() {
        every {
            getProjectReportVerificationNotificationInteractor.getLastVerificationNotification(
                PROJECT_ID,
                REPORT_ID
            )
        } returns verificationNotification
        assertThat(
            projectReportVerificationNotificationController.getLastProjectReportVerificationNotification(
                PROJECT_ID, REPORT_ID
            )
        ).isEqualTo(expectedVerificationNotification)
    }

    @Test
    fun sendNotification() {
        every {
            sendProjectReportVerificationNotificationInteractor.sendVerificationDoneByJsNotification(
                PROJECT_ID,
                REPORT_ID
            )
        } returns verificationNotification
        assertThat(
            projectReportVerificationNotificationController.sendVerificationDoneByJsNotification(
                PROJECT_ID, REPORT_ID
            )
        ).isEqualTo(expectedVerificationNotification)
    }
}
