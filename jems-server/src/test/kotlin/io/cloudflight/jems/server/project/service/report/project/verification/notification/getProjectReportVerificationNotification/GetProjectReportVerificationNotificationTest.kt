package io.cloudflight.jems.server.project.service.report.project.verification.notification.getProjectReportVerificationNotification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import io.cloudflight.jems.server.project.service.report.project.verification.notification.ProjectReportVerificationNotificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime


class GetProjectReportVerificationNotificationTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 10L
        private const val REPORT_ID = 11L
        private val createdAt = ZonedDateTime.now()

        val user = UserSimple(
            id = 1L, name = "test name",
            surname = "test surname",
            email = "test@email.com"
        )

        val verificationNotification = ProjectReportVerificationNotification(
            id = 1L, reportId = REPORT_ID, triggeredByUser = user, createdAt = createdAt
        )
    }

    @MockK
    lateinit var projectReportVerificationNotificationPersistence: ProjectReportVerificationNotificationPersistence

    @InjectMockKs
    lateinit var getProjectReportVerificationNotification: GetProjectReportVerificationNotification

    @Test
    fun getLastVerificationNotification() {
        every {
            projectReportVerificationNotificationPersistence.getLastVerificationNotificationMetaData(
                REPORT_ID
            )
        } returns verificationNotification
        assertThat(
            getProjectReportVerificationNotification.getLastVerificationNotification(
                PROJECT_ID,
                REPORT_ID
            )
        ).isEqualTo(verificationNotification)
    }
}
