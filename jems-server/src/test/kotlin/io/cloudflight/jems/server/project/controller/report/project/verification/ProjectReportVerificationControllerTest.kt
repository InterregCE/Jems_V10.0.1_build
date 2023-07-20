package io.cloudflight.jems.server.project.controller.report.project.verification

import io.cloudflight.jems.api.project.dto.report.project.verification.ProjectReportVerificationClarificationDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.ProjectReportVerificationConclusionDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationClarification.GetProjectReportVerificationClarificationInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationConclusion.GetProjectReportVerificationConclusionInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationClarification.UpdateProjectReportVerificationClarificationInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationConclusion.UpdateProjectReportVerificationConclusionInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ProjectReportVerificationControllerTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 3310L
        private const val REPORT_ID = 736L

        private val TODAY = LocalDate.now()
        private val YESTERDAY = LocalDate.now().minusDays(1)


        private val verificationConclusion =  ProjectReportVerificationConclusion(
            startDate = YESTERDAY,
            conclusionJS = "js",
            conclusionMA = "ma",
            verificationFollowUp = "none"

        )


        private val clarifications = listOf(
            ProjectReportVerificationClarification(
                id = 1L,
                number = 1,
                requestDate = YESTERDAY,
                answerDate = TODAY,
                comment = "all good"
            ),
            ProjectReportVerificationClarification(
                id = 2L,
                number = 2,
                requestDate = YESTERDAY,
                answerDate = null,
                comment = "any news?"
            )
        )

    }


    @MockK
    lateinit var getProjectReportVerificationConclusion: GetProjectReportVerificationConclusionInteractor

    @MockK
    lateinit var updateProjectReportVerificationConclusion: UpdateProjectReportVerificationConclusionInteractor

    @MockK
    lateinit var getProjectReportVerificationClarification: GetProjectReportVerificationClarificationInteractor

    @MockK
    lateinit var updateProjectReportVerificationClarification: UpdateProjectReportVerificationClarificationInteractor

    @InjectMockKs
    lateinit var controller: ProjectReportVerificationController



    @Test
    fun getReportVerificationConclusion() {
        every { getProjectReportVerificationConclusion.getVerificationConclusion(projectId = PROJECT_ID, reportId = REPORT_ID) } returns verificationConclusion
        assertThat(controller.getReportVerificationConclusion(PROJECT_ID, REPORT_ID)).isEqualTo(
            ProjectReportVerificationConclusionDTO(
                startDate = YESTERDAY,
                conclusionJS = "js",
                conclusionMA = "ma",
                verificationFollowUp = "none"

            )
        )
    }

    @Test
    fun updateReportVerificationConclusion() {
        every {
            updateProjectReportVerificationConclusion.updateVerificationConclusion(
                projectId = PROJECT_ID,
                reportId = REPORT_ID,
                conclusion = any()
            )
        } returns verificationConclusion

        assertThat(
            controller.updateReportVerificationConclusion(
                PROJECT_ID, REPORT_ID, conclusion = ProjectReportVerificationConclusionDTO(
                    startDate = YESTERDAY,
                    conclusionJS = "js",
                    conclusionMA = "ma",
                    verificationFollowUp = "none"

                )
            )
        ).isEqualTo(
            ProjectReportVerificationConclusionDTO(
                startDate = YESTERDAY,
                conclusionJS = "js",
                conclusionMA = "ma",
                verificationFollowUp = "none"

            )
        )
    }

    @Test
    fun getReportVerificationClarificationRequests() {
        every {
            getProjectReportVerificationClarification.getClarifications(
                projectId = PROJECT_ID,
                reportId = REPORT_ID
            )
        } returns clarifications

        assertThat(controller.getReportVerificationClarificationRequests(PROJECT_ID, REPORT_ID)).containsExactly(
            ProjectReportVerificationClarificationDTO(
                id = 1L,
                number = 1,
                requestDate = YESTERDAY,
                answerDate = TODAY,
                comment = "all good"
            ),
            ProjectReportVerificationClarificationDTO(
                id = 2L,
                number = 2,
                requestDate = YESTERDAY,
                answerDate = null,
                comment = "any news?"
            )
        )
    }


    @Test
    fun updateReportVerificationClarifications() {
        every {
            updateProjectReportVerificationClarification.updateClarifications(
                projectId = PROJECT_ID,
                reportId = REPORT_ID,
                clarifications = any()
            )
        } returns clarifications

        assertThat(
            controller.updateReportVerificationClarifications(PROJECT_ID, REPORT_ID, clarifications = listOf(
                    ProjectReportVerificationClarificationDTO(
                        id = 1L,
                        number = 1,
                        requestDate = YESTERDAY,
                        answerDate = TODAY,
                        comment = "all good"
                    ),
                    ProjectReportVerificationClarificationDTO(
                        id = 2L,
                        number = 2,
                        requestDate = YESTERDAY,
                        answerDate = null,
                        comment = "any news?"
                    )
                )
            )
        ).containsExactly(
            ProjectReportVerificationClarificationDTO(
                id = 1L,
                number = 1,
                requestDate = YESTERDAY,
                answerDate = TODAY,
                comment = "all good"
            ),
            ProjectReportVerificationClarificationDTO(
                id = 2L,
                number = 2,
                requestDate = YESTERDAY,
                answerDate = null,
                comment = "any news?"
            )
        )
    }

}