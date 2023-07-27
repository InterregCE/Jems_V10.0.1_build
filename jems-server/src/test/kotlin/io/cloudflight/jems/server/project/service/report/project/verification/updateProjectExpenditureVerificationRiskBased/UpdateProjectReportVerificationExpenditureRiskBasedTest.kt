package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectExpenditureVerificationRiskBased

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased.UpdateProjectReportVerificationExpenditureRiskBased
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateProjectReportVerificationExpenditureRiskBasedTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PROJECT_REPORT_ID = 20L

        val riskBasedData = ProjectReportVerificationRiskBased(
            projectReportId = PROJECT_REPORT_ID,
            riskBasedVerification = false,
            riskBasedVerificationDescription = "VERIFICATION COMM"
        )
    }

    @MockK
    lateinit var projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @InjectMockKs
    lateinit var updateProjectReportVerificationExpenditureRiskBased: UpdateProjectReportVerificationExpenditureRiskBased

    @Test
    fun updateExpenditureVerificationRiskBased() {
        every {
            projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerificationRiskBased(
                PROJECT_ID,
                PROJECT_REPORT_ID,
                riskBasedData
            )
        } returns riskBasedData

        assertThat(
            updateProjectReportVerificationExpenditureRiskBased.updateExpenditureVerificationRiskBased(
                PROJECT_ID,
                PROJECT_REPORT_ID,
                riskBasedData
            )
        ).isEqualTo(
            riskBasedData
        )
    }
}
