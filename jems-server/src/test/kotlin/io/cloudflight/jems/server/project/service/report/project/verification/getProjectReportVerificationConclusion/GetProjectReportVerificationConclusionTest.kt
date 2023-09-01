package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationConclusion

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class GetProjectReportVerificationConclusionTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 3310L
        private const val REPORT_ID = 276L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val verificationConclusion = ProjectReportVerificationConclusion(
            startDate = YESTERDAY,
            conclusionJS =  "conclusion js",
            conclusionMA = "conclusion ma",
            verificationFollowUp = "none"
        )

        fun report(id: Long, status: ProjectReportStatus): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.id } returns id
            every { report.status } returns status
            return report
        }
    }

    @MockK
    lateinit var verificationPersistence: ProjectReportVerificationPersistence

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectReportVerificationConclusion


    @Test
    fun getVerificationConclusion() {
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report(REPORT_ID, ProjectReportStatus.InVerification)
        every {
            verificationPersistence.getVerificationConclusion(
                projectId = PROJECT_ID,
                reportId = REPORT_ID
            )
        } returns verificationConclusion

        assertThat(interactor.getVerificationConclusion(PROJECT_ID, REPORT_ID)).isEqualTo(
            ProjectReportVerificationConclusion(
                startDate = YESTERDAY,
                conclusionJS = "conclusion js",
                conclusionMA = "conclusion ma",
                verificationFollowUp = "none"
            )
        )
    }

    @Test
    fun `getVerification - incorrect status should throw exception`() {
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report(REPORT_ID, ProjectReportStatus.Submitted)
        assertThrows<ReportVerificationStatusNotValidException> { interactor.getVerificationConclusion(PROJECT_ID, REPORT_ID)}
    }
}
