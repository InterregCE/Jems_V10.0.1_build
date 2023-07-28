package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectExpenditureVerificationRiskBased

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased.UpdateProjectReportVerificationExpenditureRiskBased
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateProjectReportVerificationExpenditureRiskBasedTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PROJECT_REPORT_ID = 20L
        private const val description = "VERIFICATION COMM"
        private val descriptionWithError = "a".repeat(5001)
        fun riskBasedData(description: String) = ProjectReportVerificationRiskBased(
            projectReportId = PROJECT_REPORT_ID,
            riskBasedVerification = false,
            riskBasedVerificationDescription = description
        )
    }

    @MockK
    lateinit var projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @InjectMockKs
    lateinit var  generalValidatorService: GeneralValidatorDefaultImpl

    @InjectMockKs
    lateinit var updateProjectReportVerificationExpenditureRiskBased: UpdateProjectReportVerificationExpenditureRiskBased

    @Test
    fun updateExpenditureVerificationRiskBased() {
        every {
            projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerificationRiskBased(
                PROJECT_ID,
                PROJECT_REPORT_ID,
                riskBasedData(description)
            )
        } returns riskBasedData(description)

        assertThat(
            updateProjectReportVerificationExpenditureRiskBased.updateExpenditureVerificationRiskBased(
                PROJECT_ID,
                PROJECT_REPORT_ID,
                riskBasedData(description)
            )
        ).isEqualTo(
            riskBasedData(description)
        )
    }

    @Test
    fun `updateExpenditureVerificationRiskBased - throw maxLength exception`() {
        every {
            projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerificationRiskBased(
                PROJECT_ID,
                PROJECT_REPORT_ID,
                riskBasedData(descriptionWithError)
            )
        } returns riskBasedData(descriptionWithError)

        assertThrows<AppInputValidationException> {
            updateProjectReportVerificationExpenditureRiskBased.updateExpenditureVerificationRiskBased(
                PROJECT_ID,
                PROJECT_REPORT_ID,
                riskBasedData(descriptionWithError)
            )
        }
    }
}
