package io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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

    @MockK private lateinit var paymentAccountPersistence: PaymentAccountPersistence
    @MockK private lateinit var correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @InjectMockKs private lateinit var getPaymentAccountCurrentOverview: GetPaymentAccountCurrentOverview

    @BeforeEach
    fun resetMocks() {
        clearMocks(paymentAccountPersistence, correctionLinkingPersistence)
    }

    @ParameterizedTest(name = "getCurrentOverview (status {0})")
    @EnumSource(value = PaymentAccountStatus::class, names = ["DRAFT"])
    fun `getCurrentOverview - Draft`(status: PaymentAccountStatus) {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID).status } returns status

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
                amountsGroupedByPriority = listOf(
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P01",
                        totalEligibleExpenditure = BigDecimal.valueOf(20L),
                        totalPublicContribution = BigDecimal.valueOf(17L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P02",
                        totalEligibleExpenditure = BigDecimal.valueOf(40L),
                        totalPublicContribution = BigDecimal.valueOf(34L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P03",
                        totalEligibleExpenditure = BigDecimal.valueOf(60L),
                        totalPublicContribution = BigDecimal.valueOf(51L),
                    ),
                ),
                totals = PaymentAccountAmountSummaryLine(
                    priorityAxis = null,
                    totalEligibleExpenditure = BigDecimal.valueOf(120L),
                    totalPublicContribution = BigDecimal.valueOf(102L),
                ),
            )
        )
    }

    @ParameterizedTest(name = "getCurrentOverview (status {0})")
    @EnumSource(value = PaymentAccountStatus::class, names = ["DRAFT"], mode = EnumSource.Mode.EXCLUDE)
    fun `getCurrentOverview - Finished`(status: PaymentAccountStatus) {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID).status } returns status

        val amountsSummary = mapOf<Long?, PaymentAccountAmountSummaryLine>(
            1L to summaryLine("P01", BigDecimal.valueOf(20), BigDecimal.valueOf(10)),
            2L to summaryLine("P02", BigDecimal.valueOf(30), BigDecimal.valueOf(11)),
            3L to summaryLine("P03", BigDecimal.valueOf(40), BigDecimal.valueOf(12)),
        )
        every { correctionLinkingPersistence.getTotalsForFinishedPaymentAccount(PAYMENT_ACCOUNT_ID) } returns amountsSummary

        assertThat(getPaymentAccountCurrentOverview.getCurrentOverview(PAYMENT_ACCOUNT_ID)).isEqualTo(
            PaymentAccountAmountSummary(
                amountsGroupedByPriority = listOf(
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P01",
                        totalEligibleExpenditure = BigDecimal.valueOf(20L),
                        totalPublicContribution = BigDecimal.valueOf(10L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P02",
                        totalEligibleExpenditure = BigDecimal.valueOf(30L),
                        totalPublicContribution = BigDecimal.valueOf(11L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P03",
                        totalEligibleExpenditure = BigDecimal.valueOf(40L),
                        totalPublicContribution = BigDecimal.valueOf(12L),
                    ),
                ),
                totals = PaymentAccountAmountSummaryLine(
                    priorityAxis = null,
                    totalEligibleExpenditure = BigDecimal.valueOf(90L),
                    totalPublicContribution = BigDecimal.valueOf(33L),
                ),
            )
        )
    }
}
