package io.cloudflight.jems.server.payments.controller.account.finance

import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.PaymentAccountReconciliationTypeDTO
import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountByTypeDTO
import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountPerPriorityDTO
import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.controller.account.finance.reconciliation.PaymentAccountReconciliationController
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliationType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountByType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountUpdate
import io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview.GetReconciliationOverviewInteractor
import io.cloudflight.jems.server.payments.service.account.finance.reconciliation.updateReconciliation.UpdatePaymentReconciliationInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentAccountReconciliationControllerTest : UnitTest() {

    companion object {
        private const val PAYMENT_ACCOUNT_ID = 1L
        private const val PRIORITY_AXIS_ID = 2L

        private val reconciledAmountPerPriority = ReconciledAmountPerPriority(
            priorityAxis = "PO1",
            reconciledAmountTotal = ReconciledAmountByType(
                scenario4Sum = BigDecimal(10),
                scenario3Sum = BigDecimal(11),
                clericalMistakesSum = BigDecimal(12),
                comment = "Comment total",
            ),
            reconciledAmountOfAa = ReconciledAmountByType(
                scenario4Sum = BigDecimal(1),
                scenario3Sum = BigDecimal(2),
                clericalMistakesSum = BigDecimal(3),
                comment = "Comment AA",
            ),
            reconciledAmountOfEc = ReconciledAmountByType(
                scenario4Sum = BigDecimal(5),
                scenario3Sum = BigDecimal(6),
                clericalMistakesSum = BigDecimal(7),
                comment = "Comment EC",
            )
        )

        private val expectedReconciledAmountPerPriority = ReconciledAmountPerPriorityDTO(
            priorityAxis = "PO1",

            reconciledAmountTotal = ReconciledAmountByTypeDTO(
                scenario4Sum = BigDecimal(10),
                scenario3Sum = BigDecimal(11),
                clericalMistakesSum = BigDecimal(12),
                comment = "Comment total",
            ),
            reconciledAmountOfAa = ReconciledAmountByTypeDTO(
                scenario4Sum = BigDecimal(1),
                scenario3Sum = BigDecimal(2),
                clericalMistakesSum = BigDecimal(3),
                comment = "Comment AA",
            ),
            reconciledAmountOfEc = ReconciledAmountByTypeDTO(
                scenario4Sum = BigDecimal(5),
                scenario3Sum = BigDecimal(6),
                clericalMistakesSum = BigDecimal(7),
                comment = "Comment EC",
            )
        )

        private val reconciliationUpdate = ReconciledAmountUpdate(
            priorityAxisId = PRIORITY_AXIS_ID,
            type = PaymentAccountReconciliationType.Total,
            comment = "Updated comment"
        )
        private val reconciliationUpdateDTO = ReconciledAmountUpdateDTO(
            priorityAxisId = PRIORITY_AXIS_ID,
            type = PaymentAccountReconciliationTypeDTO.Total,
            comment = "Updated comment"
        )

    }

    @MockK
    lateinit var getReconciliationOverview: GetReconciliationOverviewInteractor

    @MockK
    lateinit var updateReconciliation: UpdatePaymentReconciliationInteractor

    @InjectMockKs
    lateinit var controller: PaymentAccountReconciliationController

    @Test
    fun getReconciliationOverview() {
        every { getReconciliationOverview.getReconciliationOverview(PAYMENT_ACCOUNT_ID) } returns listOf(
            reconciledAmountPerPriority
        )

        assertThat(controller.getReconciliationOverview(PAYMENT_ACCOUNT_ID)).containsExactly(
                expectedReconciledAmountPerPriority

        )
    }

    @Test
    fun updateReconciliationComment() {
        every { updateReconciliation.updatePaymentReconciliation(PAYMENT_ACCOUNT_ID, reconciliationUpdate) } answers { }

        controller.updateReconciliationComment(PAYMENT_ACCOUNT_ID, reconciliationUpdateDTO)

        verify(exactly = 1) { updateReconciliation.updatePaymentReconciliation(PAYMENT_ACCOUNT_ID, reconciliationUpdate) }
    }

}
