package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class UpdateProjectReportVerificationExpenditureRiskBasedTest : UnitTest() {

    companion object {
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
    private lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @InjectMockKs
    lateinit var  generalValidatorService: GeneralValidatorDefaultImpl

    @InjectMockKs
    private lateinit var updateProjectReportVerificationExpenditureRiskBased: UpdateProjectReportVerificationExpenditureRiskBased

    @ParameterizedTest(name = "updateExpenditureVerificationRiskBased - {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"])
    fun updateExpenditureVerificationRiskBased(status: ProjectReportStatus) {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { reportPersistence.getReportByIdUnSecured(PROJECT_REPORT_ID) } returns report

        every {
            projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerificationRiskBased(
                PROJECT_REPORT_ID,
                riskBasedData(description)
            )
        } returns riskBasedData(description)

        assertThat(
            updateProjectReportVerificationExpenditureRiskBased.updateExpenditureVerificationRiskBased(
                PROJECT_REPORT_ID,
                riskBasedData(description)
            )
        ).isEqualTo(
            riskBasedData(description)
        )
    }

    @ParameterizedTest(name = "updateExpenditureVerificationRiskBased - wrong status {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"], mode = EnumSource.Mode.EXCLUDE)
    fun `updateExpenditureVerificationRiskBased - wrong status`(status: ProjectReportStatus) {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { reportPersistence.getReportByIdUnSecured(PROJECT_REPORT_ID) } returns report

        assertThrows<VerificationNotOpen> {
            updateProjectReportVerificationExpenditureRiskBased.updateExpenditureVerificationRiskBased(
                PROJECT_REPORT_ID,
                riskBasedData(description),
            )
        }
    }

    @Test
    fun `updateExpenditureVerificationRiskBased - throw maxLength exception`() {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.InVerification
        every { reportPersistence.getReportByIdUnSecured(PROJECT_REPORT_ID) } returns report

        every {
            projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerificationRiskBased(
                PROJECT_REPORT_ID,
                riskBasedData(descriptionWithError)
            )
        } returns riskBasedData(descriptionWithError)

        assertThrows<AppInputValidationException> {
            updateProjectReportVerificationExpenditureRiskBased.updateExpenditureVerificationRiskBased(
                PROJECT_REPORT_ID,
                riskBasedData(descriptionWithError)
            )
        }
    }
}
