package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountByType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledPriority
import io.cloudflight.jems.server.payments.repository.account.correction.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.service.account.accountingYear
import io.cloudflight.jems.server.payments.service.account.programmeFund
import io.cloudflight.jems.server.payments.service.account.submissionToSfcDateUpdated
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetReconciliationOverviewTest : UnitTest() {

    companion object {
        private const val PRIORITY_AXIS = "P01"
        private const val PRIORITY_AXIS_2 = "P02"
        private const val PRIORITY_ID = 1L
        private const val PRIORITY_ID_2 = 2L

        val g3 = listOf(
            ReconciledPriority(
                priorityId = PRIORITY_ID,
                priorityCode = PRIORITY_AXIS,
                total = BigDecimal(10),
                ofWhichAa = BigDecimal(3),
                ofWhichEc = BigDecimal(4),
            ),
            ReconciledPriority(
                priorityId = PRIORITY_ID_2,
                priorityCode = PRIORITY_AXIS_2,
                total = BigDecimal(10),
                ofWhichAa = BigDecimal(3),
                ofWhichEc = BigDecimal(4),
            )
        )

        val paymentAccount = PaymentAccount(
            id = PAYMENT_ACCOUNT_ID,
            fund = programmeFund,
            accountingYear = accountingYear,
            status = PaymentAccountStatus.DRAFT,
            nationalReference = "national reference",
            technicalAssistance = BigDecimal.TEN,
            submissionToSfcDate = submissionToSfcDateUpdated,
            sfcNumber = "sfc number",
            comment = "comment"
        )

        private val totalReconciliation1 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(10),
            scenario4Sum = BigDecimal(408),
            clericalMistakesSum = BigDecimal(10),
            comment = "Comment Total"
        )

        private val reconciliationOfAa1 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(3),
            scenario4Sum = BigDecimal(422),
            clericalMistakesSum = BigDecimal(3),
            comment = "Comment ofAa"
        )

        private val reconciliationOfEc1 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(4),
            scenario4Sum = BigDecimal(4),
            clericalMistakesSum = BigDecimal(4),
            comment = "Comment ofEc"
        )

        private val totalReconciliation2 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(10),
            scenario4Sum = BigDecimal(10),
            clericalMistakesSum = BigDecimal(10),
            comment = "Comment Total"
        )

        private val reconciliationOfAa2 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(3),
            scenario4Sum = BigDecimal(3),
            clericalMistakesSum = BigDecimal(3),
            comment = "Comment ofAa"
        )

        private val reconciliationOfEc2 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(4),
            scenario4Sum = BigDecimal(4),
            clericalMistakesSum = BigDecimal(4),
            comment = "Comment ofEc"
        )

        val expectedReconciledAmounts = listOf(
            ReconciledAmountPerPriority(
                priorityAxis = PRIORITY_AXIS,

                reconciledAmountTotal = totalReconciliation1,
                reconciledAmountOfAa = reconciliationOfAa1,
                reconciledAmountOfEc = reconciliationOfEc1,
            ),
            ReconciledAmountPerPriority(
                priorityAxis = PRIORITY_AXIS_2,

                reconciledAmountTotal = totalReconciliation2,
                reconciledAmountOfAa = reconciliationOfAa2,
                reconciledAmountOfEc = reconciliationOfEc2,
            ),
        )


    }

    @MockK
    lateinit var paymentAccountReconciliationOverviewService: PaymentAccountReconciliationOverviewService

    @InjectMockKs
    lateinit var interactor: GetReconciliationOverview

    @Test
    fun getReconciliationOverview() {

        every { paymentAccountReconciliationOverviewService.getReconciliationOverview(PAYMENT_ACCOUNT_ID) } returns listOf(
            ReconciledAmountPerPriority(
                priorityAxis = PRIORITY_AXIS,

                reconciledAmountTotal = totalReconciliation1,
                reconciledAmountOfAa = reconciliationOfAa1,
                reconciledAmountOfEc = reconciliationOfEc1,
            ),
            ReconciledAmountPerPriority(
                priorityAxis = PRIORITY_AXIS_2,

                reconciledAmountTotal = totalReconciliation2,
                reconciledAmountOfAa = reconciliationOfAa2,
                reconciledAmountOfEc = reconciliationOfEc2,
            )
        )

        assertThat(interactor.getReconciliationOverview(PAYMENT_ACCOUNT_ID)).isEqualTo(expectedReconciledAmounts)
    }
}
