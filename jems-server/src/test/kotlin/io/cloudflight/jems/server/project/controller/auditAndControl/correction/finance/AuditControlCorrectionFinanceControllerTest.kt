package io.cloudflight.jems.server.project.controller.auditAndControl.correction.finance

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.CorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.getProjectCorrectionFinancialDescription.GetProjectCorrectionFinancialDescriptionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription.UpdateProjectCorrectionFinancialDescriptionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescriptionUpdate
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AuditControlCorrectionFinanceControllerTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L

        private val financialDescription = ProjectCorrectionFinancialDescription(
            correctionId =  CORRECTION_ID,
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "sample comment",
            correctionType = CorrectionType.Ref1Dot15,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = null
        )

        private val financialDescriptionDTO = ProjectCorrectionFinancialDescriptionDTO(
            correctionId =  CORRECTION_ID,
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "sample comment",
            correctionType = CorrectionTypeDTO.Ref1Dot15,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = null
        )

        private val financialDescriptionUpdateDTO = ProjectCorrectionFinancialDescriptionUpdateDTO(
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "sample comment",
            correctionType = CorrectionTypeDTO.Ref1Dot15,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = null
        )

        private val financialDescriptionUpdate = ProjectCorrectionFinancialDescriptionUpdate(
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "sample comment",
            correctionType = CorrectionType.Ref1Dot15,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = null
        )
    }

    @MockK
    lateinit var getCorrectionFinancialDescription: GetProjectCorrectionFinancialDescriptionInteractor

    @MockK
    lateinit var updateCorrectionFinancialDescription: UpdateProjectCorrectionFinancialDescriptionInteractor

    @InjectMockKs
    lateinit var controller: AuditControlCorrectionFinanceController

    @Test
    fun getCorrectionFinancialDescription() {
        every { getCorrectionFinancialDescription.getCorrectionFinancialDescription(CORRECTION_ID) } returns
            financialDescription

        Assertions.assertThat(controller.getCorrectionFinancialDescription(PROJECT_ID, AUDIT_CONTROL_ID, CORRECTION_ID))
            .isEqualTo(financialDescriptionDTO)
    }

    @Test
    fun updateCorrectionFinancialDescription() {
        every { updateCorrectionFinancialDescription.updateCorrectionFinancialDescription(
            CORRECTION_ID,
            financialDescriptionUpdate
        ) } returns financialDescription

        Assertions.assertThat(controller.updateCorrectionFinancialDescription(
            PROJECT_ID, AUDIT_CONTROL_ID, CORRECTION_ID, financialDescriptionUpdateDTO
        )
        ).isEqualTo(financialDescriptionDTO)
    }
}
