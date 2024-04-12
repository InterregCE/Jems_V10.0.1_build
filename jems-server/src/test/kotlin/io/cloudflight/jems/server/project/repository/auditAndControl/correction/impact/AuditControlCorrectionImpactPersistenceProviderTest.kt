package io.cloudflight.jems.server.project.repository.auditAndControl.correction.impact

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpactAction
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuditControlCorrectionImpactPersistenceProviderTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 15L

        private fun entity() = AuditControlCorrectionEntity(
            id = 88L,
            auditControl = mockk {
                every { id } returns 75L
                every { number } returns 14
            },
            orderNr = 18,
            status = AuditControlStatus.Ongoing,
            correctionType = AuditControlCorrectionType.LinkedToInvoice,
            followUpOfCorrection = null,
            followUpOfCorrectionType = mockk(),
            repaymentDate = null,
            lateRepayment = null,
            partnerReport = null,
            lumpSum = null,
            lumpSumPartnerId = null,
            programmeFund = null,
            impact = AuditControlCorrectionImpactAction.AdjustmentInNextPayment,
            impactComment = "old comment",
            expenditure = null,
            costCategory = null,
            procurementId = null,
            projectModificationId = null,
        )

    }

    @MockK
    private lateinit var correctionRepository: AuditControlCorrectionRepository

    @InjectMockKs
    private lateinit var persistence: AuditControlCorrectionImpactPersistenceProvider

    @Test
    fun updateCorrectionImpact() {
        val entity = entity()
        every { correctionRepository.getReferenceById(CORRECTION_ID) } returns entity

        val toUpdate = AuditControlCorrectionImpact(
            action = AuditControlCorrectionImpactAction.BudgetReduction,
            comment = "new comment",
        )

        assertThat(persistence.updateCorrectionImpact(CORRECTION_ID, toUpdate)).isEqualTo(toUpdate)

        assertThat(entity.impact).isEqualTo(AuditControlCorrectionImpactAction.BudgetReduction)
        assertThat(entity.impactComment).isEqualTo("new comment")
    }

}
