package io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.sumUpProperColumns
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetPaymentAccountCurrentOverviewTest : UnitTest() {

    companion object {
        private const val PAYMENT_ACCOUNT_ID = 101L

        private fun summaryLine(priorityAxis: String?, eligible: BigDecimal, public: BigDecimal) = PaymentAccountAmountSummaryLine(
            priorityAxis = priorityAxis,
            totalEligibleExpenditure = eligible,
            totalPublicContribution = public,
        )
    }

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @InjectMockKs
    private lateinit var getPaymentAccountCurrentOverview: GetPaymentAccountCurrentOverview

    @Test
    fun `getCurrentOverview - Draft`() {
        val paymentAccount = mockk<PaymentAccount>()
        every { paymentAccount.status.isFinished() } returns false
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount

        val amountsSummaryTmp = mapOf<Long?, PaymentAccountAmountSummaryLineTmp>(
            1L to PaymentAccountAmountSummaryLineTmp(
                1L,
                "P01",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(4)
            ),
            2L to PaymentAccountAmountSummaryLineTmp(
                2L,
                "P02",
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(6),
                BigDecimal.valueOf(8)
            ),
            3L to PaymentAccountAmountSummaryLineTmp(
                3L,
                "P03",
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(9),
                BigDecimal.valueOf(12)
            ),
        )
        every { correctionLinkingPersistence.calculateOverviewForDraftPaymentAccount(PAYMENT_ACCOUNT_ID) } returns amountsSummaryTmp

        assertThat(getPaymentAccountCurrentOverview.getCurrentOverview(PAYMENT_ACCOUNT_ID)).isEqualTo(
            PaymentAccountAmountSummary(
                amountsGroupedByPriority = amountsSummaryTmp.sumUpProperColumns().values.toList(),
                totals = summaryLine(null, BigDecimal.valueOf(120), BigDecimal.valueOf(102))
            )
        )
    }

    @Test
    fun `getCurrentOverview - Finished`() {
        val paymentAccount = mockk<PaymentAccount>()
        every { paymentAccount.status.isFinished() } returns true
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount
        val amountsSummary = mapOf<Long?, PaymentAccountAmountSummaryLine>(
            1L to summaryLine("P01", BigDecimal.valueOf(20), BigDecimal.valueOf(10)),
            2L to summaryLine("P01", BigDecimal.valueOf(30), BigDecimal.valueOf(10)),
            3L to summaryLine("P02", BigDecimal.valueOf(40), BigDecimal.valueOf(10)),
        )
        every { correctionLinkingPersistence.getTotalsForFinishedPaymentAccount(PAYMENT_ACCOUNT_ID) } returns amountsSummary

        assertThat(getPaymentAccountCurrentOverview.getCurrentOverview(PAYMENT_ACCOUNT_ID)).isEqualTo(
            PaymentAccountAmountSummary(
                amountsGroupedByPriority = amountsSummary.values.toList(),
                totals = summaryLine(null, BigDecimal.valueOf(90), BigDecimal.valueOf(30))
            )
        )
    }
}
