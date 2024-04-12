package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.measure.CorrectionProgrammeMeasureRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpactAction
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AuditControlCreateCorrectionPersistenceProviderTest {

    companion object {
        private val correction = AuditControlCorrectionCreate(
            orderNr = 4,
            status = AuditControlStatus.Closed,
            type = AuditControlCorrectionType.LinkedToCostOption,
            followUpOfCorrectionType = CorrectionFollowUpType.CourtProcedure,
            defaultImpact = AuditControlCorrectionImpactAction.AdjustmentInNextPayment,
        )

        private val expectedCorrection = AuditControlCorrectionDetail(
            id = 0L,
            orderNr = 4,
            status = AuditControlStatus.Closed,
            type = AuditControlCorrectionType.LinkedToCostOption,

            auditControlId = 7L,
            auditControlNr = 17,

            followUpOfCorrectionId = null,
            correctionFollowUpType = CorrectionFollowUpType.CourtProcedure,
            repaymentFrom = null,
            lateRepaymentTo = null,
            partnerId = null,
            partnerReportId = null,
            lumpSumOrderNr = null,
            programmeFundId = null,
            impact = AuditControlCorrectionImpact(
                action = AuditControlCorrectionImpactAction.AdjustmentInNextPayment,
                comment = "",
            ),
            costCategory = null,
            expenditureCostItem = null,
            procurementId = null,
        )
    }

    @MockK
    private lateinit var auditControlRepository: AuditControlRepository
    @MockK
    private lateinit var auditControlCorrectionRepository: AuditControlCorrectionRepository
    @MockK
    private lateinit var auditControlCorrectionFinanceRepository: ProjectCorrectionFinancialDescriptionRepository
    @MockK
    private lateinit var auditControlCorrectionMeasureRepository: CorrectionProgrammeMeasureRepository

    @InjectMockKs
    lateinit var persistence: AuditControlCreateCorrectionPersistenceProvider

    @Test
    fun createCorrection() {
        val auditControl = mockk<AuditControlEntity> {
            every { id } returns 7L
            every { number } returns 17
        }
        every { auditControlRepository.getReferenceById(7L) } returns auditControl

        val slotEnity = slot<AuditControlCorrectionEntity>()
        every { auditControlCorrectionRepository.save(capture(slotEnity)) } returnsArgument 0

        val slotFinanceEntity = slot<AuditControlCorrectionFinanceEntity>()
        every { auditControlCorrectionFinanceRepository.save(capture(slotFinanceEntity)) } returnsArgument 0

        val slotMeasureEntity = slot<AuditControlCorrectionMeasureEntity>()
        every { auditControlCorrectionMeasureRepository.save(capture(slotMeasureEntity)) } returnsArgument 0

        assertThat(persistence.createCorrection(7L, correction)).isEqualTo(expectedCorrection)

        assertThat(slotEnity.captured.auditControl).isEqualTo(auditControl)
        assertThat(slotEnity.captured.orderNr).isEqualTo(4)
        assertThat(slotEnity.captured.status).isEqualTo(AuditControlStatus.Closed)
        assertThat(slotEnity.captured.correctionType).isEqualTo(AuditControlCorrectionType.LinkedToCostOption)
        assertThat(slotEnity.captured.followUpOfCorrection).isNull()
        assertThat(slotEnity.captured.followUpOfCorrectionType).isEqualTo(CorrectionFollowUpType.CourtProcedure)
        assertThat(slotEnity.captured.repaymentDate).isNull()
        assertThat(slotEnity.captured.lateRepayment).isNull()
        assertThat(slotEnity.captured.partnerReport).isNull()
        assertThat(slotEnity.captured.programmeFund).isNull()
        assertThat(slotEnity.captured.impact).isEqualTo(AuditControlCorrectionImpactAction.AdjustmentInNextPayment)
        assertThat(slotEnity.captured.impactComment).isEmpty()

        assertThat(slotFinanceEntity.captured.correction).isEqualTo(slotEnity.captured)
        assertThat(slotFinanceEntity.captured.deduction).isTrue()
        assertThat(slotFinanceEntity.captured.fundAmount).isEqualTo(BigDecimal.ZERO)
        assertThat(slotFinanceEntity.captured.publicContribution).isEqualTo(BigDecimal.ZERO)
        assertThat(slotFinanceEntity.captured.autoPublicContribution).isEqualTo(BigDecimal.ZERO)
        assertThat(slotFinanceEntity.captured.privateContribution).isEqualTo(BigDecimal.ZERO)
        assertThat(slotFinanceEntity.captured.infoSentBeneficiaryDate).isNull()
        assertThat(slotFinanceEntity.captured.infoSentBeneficiaryComment).isNull()
        assertThat(slotFinanceEntity.captured.correctionType).isNull()
        assertThat(slotFinanceEntity.captured.clericalTechnicalMistake).isFalse()
        assertThat(slotFinanceEntity.captured.goldPlating).isFalse()
        assertThat(slotFinanceEntity.captured.suspectedFraud).isFalse()
        assertThat(slotFinanceEntity.captured.correctionComment).isNull()

        assertThat(slotMeasureEntity.captured.correction).isEqualTo(slotEnity.captured)
        assertThat(slotMeasureEntity.captured.scenario).isEqualTo(ProjectCorrectionProgrammeMeasureScenario.NA)
        assertThat(slotMeasureEntity.captured.comment).isNull()
    }

}
