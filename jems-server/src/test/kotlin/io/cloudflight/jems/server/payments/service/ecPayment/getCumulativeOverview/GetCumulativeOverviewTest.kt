package io.cloudflight.jems.server.payments.service.ecPayment.getCumulativeOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview.GetCumulativeOverview
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetCumulativeOverviewTest: UnitTest() {

    companion object {
        private const val PAYMENT_TO_EC_ID = 3L

        private val currentOverviewValues = listOf(
            PaymentToEcAmountSummaryLineTmp(
                priorityAxis = "PO1",
                fundAmount = BigDecimal.valueOf(101),
                partnerContribution = BigDecimal(201),
                ofWhichPublic = BigDecimal(301),
                ofWhichAutoPublic = BigDecimal(401)
            ),
            PaymentToEcAmountSummaryLineTmp(
                priorityAxis = "PO2",
                fundAmount = BigDecimal.valueOf(102),
                partnerContribution = BigDecimal(202),
                ofWhichPublic = BigDecimal(302),
                ofWhichAutoPublic = BigDecimal(402)
            ),
        )


        private val paymentToEcAmountSummaryTmpMap = mapOf(
            Pair(
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                currentOverviewValues
            )
        )

        private val cumulativeValues = listOf(
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO1",
                totalEligibleExpenditure = BigDecimal(110),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(99)
            ),
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO2",
                totalEligibleExpenditure = BigDecimal(1230),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(871)
            )
        )

        private val expectedCumulativeOverview = PaymentToEcAmountSummary(
            amountsGroupedByPriority = listOf(
                PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO1",
                    totalEligibleExpenditure = BigDecimal(412),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(902)
                ),
                PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO2",
                    totalEligibleExpenditure = BigDecimal(1534),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(1677)
                )
            ),
            totals = PaymentToEcAmountSummaryLine(
                priorityAxis = null,
                totalEligibleExpenditure = BigDecimal(1946),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(2579)
            )
        )
    }


    @MockK
    lateinit var ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence

    @InjectMockKs
    lateinit var getCumulativeOverview: GetCumulativeOverview


    @Test
    fun getCumulativeOverview() {
        every { ecPaymentLinkPersistence.calculateAndGetOverview(PAYMENT_TO_EC_ID)
        } returns paymentToEcAmountSummaryTmpMap

        every { ecPaymentLinkPersistence.getCumulativeTotalForEcPayment(PAYMENT_TO_EC_ID) } returns cumulativeValues

        assertThat(getCumulativeOverview.getCumulativeOverview(PAYMENT_TO_EC_ID)).isEqualTo(expectedCumulativeOverview)

    }
}