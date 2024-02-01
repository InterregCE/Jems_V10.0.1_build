package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetCumulativeOverviewTest: UnitTest() {

    companion object {

        private val paymentToEcAmountSummaryTmpMap = mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf(
                174L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 174L,
                    priorityAxis = "PO1",
                    fundAmount = BigDecimal.valueOf(52),
                    partnerContribution = BigDecimal(53),
                    ofWhichPublic = BigDecimal(54),
                    ofWhichAutoPublic = BigDecimal(55),
                    correctedFundAmount = BigDecimal(50),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(105)
                ),
                null to PaymentToEcAmountSummaryLineTmp(
                    priorityId = null,
                    priorityAxis = null,
                    fundAmount = BigDecimal.valueOf(70),
                    partnerContribution = BigDecimal(69),
                    ofWhichPublic = BigDecimal(68),
                    ofWhichAutoPublic = BigDecimal(67),
                    correctedFundAmount = BigDecimal(50),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(139)
                ),
            ),
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
                174L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 174L,
                    priorityAxis = "PO1",
                    fundAmount = BigDecimal.valueOf(5),
                    partnerContribution = BigDecimal(5),
                    ofWhichPublic = BigDecimal(5),
                    ofWhichAutoPublic = BigDecimal(5),
                    correctedFundAmount = BigDecimal(50),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(10)
                ),
                175L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 175L,
                    priorityAxis = "PO2",
                    fundAmount = BigDecimal.valueOf(25),
                    partnerContribution = BigDecimal(26),
                    ofWhichPublic = BigDecimal(27),
                    ofWhichAutoPublic = BigDecimal(28),
                    correctedFundAmount = BigDecimal(50),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(51)
                ),
            ),
        )

        private val paymentToEcAmountSummaryTmpMapFallsUnder94Or95 = mapOf(
            PaymentToEcOverviewType.FallsUnderArticle94Or95 to mapOf(
                174L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 174L,
                    priorityAxis = "PO1",
                    fundAmount = BigDecimal.valueOf(52),
                    partnerContribution = BigDecimal(53),
                    ofWhichPublic = BigDecimal(54),
                    ofWhichAutoPublic = BigDecimal(55),
                    correctedFundAmount = BigDecimal(50),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(105)
                ),
                null to PaymentToEcAmountSummaryLineTmp(
                    priorityId = null,
                    priorityAxis = null,
                    fundAmount = BigDecimal.valueOf(70),
                    partnerContribution = BigDecimal(69),
                    ofWhichPublic = BigDecimal(68),
                    ofWhichAutoPublic = BigDecimal(67),
                    correctedFundAmount = BigDecimal(50),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(139)
                ),
            ),
            PaymentToEcOverviewType.Correction to mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
                174L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 174L,
                    priorityAxis = "PO1",
                    fundAmount = BigDecimal.valueOf(5),
                    partnerContribution = BigDecimal(5),
                    ofWhichPublic = BigDecimal(5),
                    ofWhichAutoPublic = BigDecimal(5),
                    correctedFundAmount = BigDecimal(50),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(10)
                ),
                175L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 175L,
                    priorityAxis = "PO2",
                    fundAmount = BigDecimal.valueOf(25),
                    partnerContribution = BigDecimal(26),
                    ofWhichPublic = BigDecimal(27),
                    ofWhichAutoPublic = BigDecimal(28),
                    correctedFundAmount = BigDecimal(50),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(51)
                ),
            ),
        )

        private val cumulativeValues = mapOf(
            175L to PaymentToEcAmountSummaryLine(
                priorityAxis = "PO2",
                totalEligibleExpenditure = BigDecimal(110),
                totalUnionContribution = BigDecimal.valueOf(105),
                totalPublicContribution = BigDecimal(99),
            ),
            176L to PaymentToEcAmountSummaryLine(
                priorityAxis = "PO3",
                totalEligibleExpenditure = BigDecimal(1230),
                totalUnionContribution = BigDecimal.valueOf(1014),
                totalPublicContribution = BigDecimal(871),
            ),
            null to PaymentToEcAmountSummaryLine(
                priorityAxis = null,
                totalEligibleExpenditure = BigDecimal(5),
                totalUnionContribution = BigDecimal.valueOf(6),
                totalPublicContribution = BigDecimal(7),
            ),
        )

        private val expectedCumulativeOverview = PaymentToEcAmountSummary(
            amountsGroupedByPriority = listOf(
                PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO1",
                    totalEligibleExpenditure = BigDecimal(10),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(15),
                ),
                PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO2",
                    totalEligibleExpenditure = BigDecimal(161),
                    totalUnionContribution = BigDecimal.valueOf(105),
                    totalPublicContribution = BigDecimal(179),
                ),
                PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO3",
                    totalEligibleExpenditure = BigDecimal(1230),
                    totalUnionContribution = BigDecimal.valueOf(1014),
                    totalPublicContribution = BigDecimal(871),
                ),
                PaymentToEcAmountSummaryLine(
                    priorityAxis = null,
                    totalEligibleExpenditure = BigDecimal(5),
                    totalUnionContribution = BigDecimal.valueOf(6),
                    totalPublicContribution = BigDecimal(7),
                ),
            ),
            totals = PaymentToEcAmountSummaryLine(
                priorityAxis = null,
                totalEligibleExpenditure = BigDecimal(1406),
                totalUnionContribution = BigDecimal.valueOf(1125),
                totalPublicContribution = BigDecimal(1072),
            ),
        )

        private val expectedCumulativeOverviewFor94Or95 = PaymentToEcAmountSummary(
            amountsGroupedByPriority = listOf(
                PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO1",
                    totalEligibleExpenditure = BigDecimal(115),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(219),
                ),
                PaymentToEcAmountSummaryLine(
                    priorityAxis = null,
                    totalEligibleExpenditure = BigDecimal(144),
                    totalUnionContribution = BigDecimal.valueOf(6),
                    totalPublicContribution = BigDecimal(192),
                ),
                PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO2",
                    totalEligibleExpenditure = BigDecimal(161),
                    totalUnionContribution = BigDecimal.valueOf(105),
                    totalPublicContribution = BigDecimal(204),
                ),
                PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO3",
                    totalEligibleExpenditure = BigDecimal(1230),
                    totalUnionContribution = BigDecimal.valueOf(1014),
                    totalPublicContribution = BigDecimal(871),
                ),
            ),
            totals = PaymentToEcAmountSummaryLine(
                priorityAxis = null,
                totalEligibleExpenditure = BigDecimal(1650),
                totalUnionContribution = BigDecimal.valueOf(1125),
                totalPublicContribution = BigDecimal(1486),
            ),
        )
    }


    @MockK
    private lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence
    @MockK
    private lateinit var ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence

    @InjectMockKs
    private lateinit var getCumulativeOverview: GetCumulativeOverview


    @Test
    fun `getCumulativeOverview - draft`() {
        val paymentId = 15L
        val paymentDraft = mockk<PaymentApplicationToEcDetail>()
        every { paymentDraft.status } returns PaymentEcStatus.Draft
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentId) } returns paymentDraft

        every { ecPaymentLinkPersistence.getCumulativeTotalForEcPayment(paymentId) } returns cumulativeValues
        every { ecPaymentLinkPersistence.calculateAndGetOverviewForDraftEcPayment(paymentId) } returns paymentToEcAmountSummaryTmpMap

        assertThat(getCumulativeOverview.getCumulativeOverview(paymentId)).isEqualTo(expectedCumulativeOverview)
    }

    @Test
    fun `getCumulativeOverview - draft with Art94Or95`() {
        val paymentId = 15L
        val paymentDraft = mockk<PaymentApplicationToEcDetail>()
        every { paymentDraft.status } returns PaymentEcStatus.Draft
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentId) } returns paymentDraft

        every { ecPaymentLinkPersistence.getCumulativeTotalForEcPayment(paymentId) } returns cumulativeValues
        every { ecPaymentLinkPersistence.calculateAndGetOverviewForDraftEcPayment(paymentId) } returns paymentToEcAmountSummaryTmpMapFallsUnder94Or95

        assertThat(getCumulativeOverview.getCumulativeOverview(paymentId)).isEqualTo(expectedCumulativeOverviewFor94Or95)
    }
}
