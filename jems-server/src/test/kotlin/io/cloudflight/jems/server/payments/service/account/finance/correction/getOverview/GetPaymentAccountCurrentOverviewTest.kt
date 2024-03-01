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
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

class GetPaymentAccountCurrentOverviewTest : UnitTest() {

    companion object {
        private const val PAYMENT_ACCOUNT_ID = 101L


        val summary =  PaymentAccountAmountSummary(
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
    }

    @MockK private lateinit var service: PaymentAccountCorrectionsOverviewService

    @InjectMockKs private lateinit var getPaymentAccountCurrentOverview: GetPaymentAccountCurrentOverview

    @BeforeEach
    fun resetMocks() {
        clearMocks(service)
    }

    @Test
    fun getCurrentOverview() {
        every { service.getCurrentOverview(PAYMENT_ACCOUNT_ID) } returns summary

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
}
