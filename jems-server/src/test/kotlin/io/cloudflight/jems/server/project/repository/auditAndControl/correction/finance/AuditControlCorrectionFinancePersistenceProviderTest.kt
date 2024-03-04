package io.cloudflight.jems.server.project.repository.auditAndControl.correction.finance

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescriptionUpdate
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class AuditControlCorrectionFinancePersistenceProviderTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 15L

        private fun correctionEntity(id: Long): AuditControlCorrectionEntity {
            val entity = mockk<AuditControlCorrectionEntity>()
            every { entity.id } returns id
            return entity
        }

        private fun entity() = AuditControlCorrectionFinanceEntity(
            correctionId = CORRECTION_ID,
            correction = correctionEntity(id = CORRECTION_ID),
            deduction = true,
            fundAmount = BigDecimal.valueOf(10L),
            publicContribution = BigDecimal.valueOf(11L),
            autoPublicContribution = BigDecimal.valueOf(12L),
            privateContribution = BigDecimal.valueOf(13L),
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "test",
            correctionType = CorrectionType.Ref7Dot5,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = "comment",
        )

        private val financialDescription = ProjectCorrectionFinancialDescription(
            correctionId = CORRECTION_ID,
            deduction = true,
            fundAmount = BigDecimal.valueOf(10L),
            publicContribution = BigDecimal.valueOf(11L),
            autoPublicContribution = BigDecimal.valueOf(12L),
            privateContribution = BigDecimal.valueOf(13L),
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "test",
            correctionType = CorrectionType.Ref7Dot5,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = "comment",
        )

        private val financialDescriptionUpdate = ProjectCorrectionFinancialDescriptionUpdate(
            deduction = false,
            fundAmount = BigDecimal.valueOf(50),
            publicContribution = BigDecimal.valueOf(51),
            autoPublicContribution = BigDecimal.valueOf(52),
            privateContribution = BigDecimal.valueOf(53),
            infoSentBeneficiaryDate = LocalDate.of(2023, 9, 14),
            infoSentBeneficiaryComment = "new comment",
            correctionType = CorrectionType.Ref1Dot1,
            clericalTechnicalMistake = true,
            goldPlating = true,
            suspectedFraud = false,
            correctionComment = "comment, new",
        )

        private val financialDescriptionAfterUpdate = ProjectCorrectionFinancialDescription(
            correctionId = CORRECTION_ID,
            deduction = false,
            fundAmount = BigDecimal.valueOf(50L),
            publicContribution = BigDecimal.valueOf(51L),
            autoPublicContribution = BigDecimal.valueOf(52L),
            privateContribution = BigDecimal.valueOf(53L),
            infoSentBeneficiaryDate = LocalDate.of(2023, 9, 14),
            infoSentBeneficiaryComment = "new comment",
            correctionType = CorrectionType.Ref1Dot1,
            clericalTechnicalMistake = true,
            goldPlating = true,
            suspectedFraud = false,
            correctionComment = "comment, new",
        )

    }

    @MockK
    lateinit var projectCorrectionFinancialDescriptionRepository: ProjectCorrectionFinancialDescriptionRepository

    @InjectMockKs
    lateinit var financialDescriptionPersistenceProvider: AuditControlCorrectionFinancePersistenceProvider

    @Test
    fun getCorrectionFinancialDescription() {
        every { projectCorrectionFinancialDescriptionRepository.getReferenceById(CORRECTION_ID) } returns entity()
        assertThat(financialDescriptionPersistenceProvider.getCorrectionFinancialDescription(CORRECTION_ID)).isEqualTo(
            financialDescription
        )
    }

    @Test
    fun updateCorrectionFinancialDescription() {
        every { projectCorrectionFinancialDescriptionRepository.getReferenceById(CORRECTION_ID) } returns entity()
        assertThat(financialDescriptionPersistenceProvider.updateCorrectionFinancialDescription(
            CORRECTION_ID,
            financialDescriptionUpdate
        )).isEqualTo(financialDescriptionAfterUpdate)
    }
}
