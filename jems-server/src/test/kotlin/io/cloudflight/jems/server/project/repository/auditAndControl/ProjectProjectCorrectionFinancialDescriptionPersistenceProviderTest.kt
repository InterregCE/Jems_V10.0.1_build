package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionFinancialDescriptionEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.financialDescription.ProjectCorrectionFinancialDescriptionPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.financialDescription.ProjectCorrectionFinancialDescriptionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
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

class ProjectProjectCorrectionFinancialDescriptionPersistenceProviderTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 1L

        private val correctionEntity = ProjectAuditControlCorrectionEntity(
            id = CORRECTION_ID,
            auditControlEntity = mockk(),
            orderNr = 99,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = false
        )

        private val entity = ProjectCorrectionFinancialDescriptionEntity(
            correctionId = CORRECTION_ID,
            correction = correctionEntity,
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "test",
            correctionType = CorrectionType.Ref7Dot5,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = ""
        )

        private val financialDescription = ProjectCorrectionFinancialDescription(
            correctionId = CORRECTION_ID,
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "test",
            correctionType = CorrectionType.Ref7Dot5,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = ""
        )

        private val financialDescriptionUpdate = ProjectCorrectionFinancialDescriptionUpdate(
            deduction = false,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "test",
            correctionType = CorrectionType.Ref1Dot1,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = ""
        )
    }

    @MockK
    lateinit var projectCorrectionFinancialDescriptionRepository: ProjectCorrectionFinancialDescriptionRepository

    @InjectMockKs
    lateinit var financialDescriptionPersistenceProvider: ProjectCorrectionFinancialDescriptionPersistenceProvider

    @Test
    fun getCorrectionFinancialDescription() {
        every { projectCorrectionFinancialDescriptionRepository.getById(CORRECTION_ID) } returns entity
        assertThat(financialDescriptionPersistenceProvider.getCorrectionFinancialDescription(CORRECTION_ID)).isEqualTo(
            financialDescription
        )
    }

    @Test
    fun updateCorrectionFinancialDescription() {
        every { projectCorrectionFinancialDescriptionRepository.getById(CORRECTION_ID) } returns entity
        assertThat(financialDescriptionPersistenceProvider.updateCorrectionFinancialDescription(
            CORRECTION_ID,
            financialDescriptionUpdate
        )).isEqualTo(financialDescription.copy(deduction = false, correctionType= CorrectionType.Ref1Dot1))
    }
}
