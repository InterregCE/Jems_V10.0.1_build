package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationConclusion

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationClarification.UpdateProjectReportVerificationClarificationTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils
import java.time.LocalDate

class UpdateProjectReportVerificationConclusionTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 3310L
        private const val REPORT_ID = 276L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val verificationConclusion = ProjectReportVerificationConclusion(
            startDate = YESTERDAY,
            conclusionJS =  "js",
            conclusionMA = "ma",
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
    lateinit var interactor: UpdateProjectReportVerificationConclusion

    @ParameterizedTest(name = "updateVerificationConclusion")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"], mode = EnumSource.Mode.INCLUDE)
    fun updateVerificationConclusion(status: ProjectReportStatus) {
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns UpdateProjectReportVerificationClarificationTest.report(REPORT_ID, status)
        every {
            verificationPersistence.updateVerificationConclusion(
                projectId = PROJECT_ID,
                reportId = REPORT_ID,
                projectReportVerificationConclusion = any()
            )
        } returns verificationConclusion

        assertThat(
            interactor.updateVerificationConclusion(
                PROJECT_ID, REPORT_ID, ProjectReportVerificationConclusion(
                    startDate = YESTERDAY,
                    conclusionJS = "js",
                    conclusionMA = "ma",
                    verificationFollowUp = "none"
                )
            )
        ).isEqualTo(
            ProjectReportVerificationConclusion(
                startDate = YESTERDAY,
                conclusionJS = "js",
                conclusionMA = "ma",
                verificationFollowUp = "none"
            )
        )
    }

    @ParameterizedTest(name = "updateVerificationConclusion - wrong status (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"], mode = EnumSource.Mode.EXCLUDE)
    fun `update conclusion - incorrect status should throw exception`(status: ProjectReportStatus) {
        every {
            reportPersistence.getReportById(
                PROJECT_ID, REPORT_ID
            )
        } returns report(REPORT_ID, status)

        assertThrows<ReportVerificationStatusNotValidException> {  interactor.updateVerificationConclusion(
            PROJECT_ID, REPORT_ID, ProjectReportVerificationConclusion(
                startDate = YESTERDAY,
                conclusionJS = "js",
                conclusionMA = "ma",
                verificationFollowUp = "none"
            )
        )}
    }

    @ParameterizedTest(name = "updateVerificationConclusion")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"], mode = EnumSource.Mode.INCLUDE)
    fun `update conclusion - invalid input should throw exception`(status: ProjectReportStatus) {
        every {
            reportPersistence.getReportById(
                PROJECT_ID, REPORT_ID
            )
        } returns report(REPORT_ID, status)

        assertThrows<ReportVerificationInvalidInputException> {  interactor.updateVerificationConclusion(
            PROJECT_ID, REPORT_ID, ProjectReportVerificationConclusion(
                startDate = YESTERDAY,
                conclusionJS = RandomStringUtils.random(5001),
                conclusionMA = RandomStringUtils.random(5001),
                verificationFollowUp = RandomStringUtils.random(5001)
            )
        )}
    }
}
