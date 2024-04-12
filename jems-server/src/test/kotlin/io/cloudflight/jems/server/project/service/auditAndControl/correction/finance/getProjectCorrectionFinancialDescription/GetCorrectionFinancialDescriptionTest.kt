package io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.getProjectCorrectionFinancialDescription

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinance
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetCorrectionFinancialDescriptionTest: UnitTest() {

    companion object {
        private const val CORRECTION_ID = 3L

        private val financialDescription = AuditControlCorrectionFinance(
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
    }

    @MockK
    lateinit var financialDescriptionPersistence: AuditControlCorrectionFinancePersistence

    @InjectMockKs
    lateinit var getCorrectionFinancialDescription: GetProjectCorrectionFinancialDescription

    @Test
    fun getCorrectionFinancialDescription() {
        every { financialDescriptionPersistence.getCorrectionFinancialDescription(CORRECTION_ID) } returns financialDescription
        assertThat(getCorrectionFinancialDescription.getCorrectionFinancialDescription(CORRECTION_ID))
            .isEqualTo(financialDescription)
    }
}
