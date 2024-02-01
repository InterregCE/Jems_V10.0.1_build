package io.cloudflight.jems.server.payments.service.ecPayment.getCumulativeAmountsByType

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getOverviewAmountsByType.GetOverviewAmountsByType
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class GetCumulativeAmountsByTypeTest : UnitTest() {

    companion object {
        private const val PAYMENT_TO_EC_ID = 3L
        private const val programmeFundId = 10L
        private const val accountingYearId = 3L
        private val submissionDate = LocalDate.now()

        private val programmeFund = ProgrammeFund(programmeFundId, true)

        private val accountingYear =
            AccountingYear(accountingYearId, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))

        private val expectedPaymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = programmeFund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private fun paymentApplicationDetail(status: PaymentEcStatus) = PaymentApplicationToEcDetail(
            id = PAYMENT_TO_EC_ID,
            status = status,
            paymentApplicationToEcSummary = expectedPaymentApplicationsToEcSummary
        )

        private val expectedPaymentsIncludedInPaymentsToEc = listOf(
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO1",
                totalEligibleExpenditure = BigDecimal(302),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(803)
            ),
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO2",
                totalEligibleExpenditure = BigDecimal(304),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(806)
            )
        )

        private val expectedPaymentsIncludedInPaymentsToEcFinished = listOf(
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO1",
                totalEligibleExpenditure = BigDecimal(302),
                totalUnionContribution = BigDecimal.valueOf(502),
                totalPublicContribution = BigDecimal(802),
            ),
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO2",
                totalEligibleExpenditure = BigDecimal(304),
                totalUnionContribution = BigDecimal.valueOf(504),
                totalPublicContribution = BigDecimal(804),
            )
        )

        private val paymentsIncludedInPaymentsToEcMap = mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf<Long?, PaymentToEcAmountSummaryLine>(
                105L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO1",
                    totalEligibleExpenditure = BigDecimal(302),
                    totalUnionContribution = BigDecimal.valueOf(502),
                    totalPublicContribution = BigDecimal(802)
                ),
                106L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO2",
                    totalEligibleExpenditure = BigDecimal(304),
                    totalUnionContribution = BigDecimal.valueOf(504),
                    totalPublicContribution = BigDecimal(804)
                ),
            ),
        )

        private val expectedTotal = PaymentToEcAmountSummaryLine(
            priorityAxis = null,
            totalEligibleExpenditure = BigDecimal(606),
            totalUnionContribution = BigDecimal.ZERO,
            totalPublicContribution = BigDecimal(1609),
        )

        private val expectedTotalFinished = PaymentToEcAmountSummaryLine(
            priorityAxis = null,
            totalEligibleExpenditure = BigDecimal(606),
            totalUnionContribution = BigDecimal.valueOf(1006),
            totalPublicContribution = BigDecimal(1606),
        )

        private val paymentToEcAmountSummaryTmpMap = mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
                105L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 105L,
                    priorityAxis = "PO1",
                    fundAmount = BigDecimal.valueOf(101),
                    partnerContribution = BigDecimal(201),
                    ofWhichPublic = BigDecimal(301),
                    ofWhichAutoPublic = BigDecimal(401),
                    correctedFundAmount = BigDecimal(405),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(302)
                ),
                106L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 106L,
                    priorityAxis = "PO2",
                    fundAmount = BigDecimal.valueOf(102),
                    partnerContribution = BigDecimal(202),
                    ofWhichPublic = BigDecimal(302),
                    ofWhichAutoPublic = BigDecimal(402),
                    correctedFundAmount = BigDecimal(405),
                    unionContribution = BigDecimal(0),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal.valueOf(304)
                ),
            ),
        )
    }

    @MockK
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence
    @MockK
    lateinit var ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence

    @InjectMockKs
    lateinit var getCumulativeAmountsByType: GetOverviewAmountsByType

    @Test
    fun `getCumulativeAmountsByType - notArt9495`() {
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(PAYMENT_TO_EC_ID) } returns paymentApplicationDetail(
            status = PaymentEcStatus.Draft
        )
        every { ecPaymentLinkPersistence.calculateAndGetOverviewForDraftEcPayment(PAYMENT_TO_EC_ID) } returns paymentToEcAmountSummaryTmpMap

        val expectedSummary = PaymentToEcAmountSummary(expectedPaymentsIncludedInPaymentsToEc, expectedTotal)

        assertThat(
            getCumulativeAmountsByType.getOverviewAmountsByType(
                paymentToEcId = PAYMENT_TO_EC_ID, type = PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95
            )
        ).isEqualTo(expectedSummary)
    }

    @Test
    fun `getCumulativeAmountsByType - summary`() {
        val expectedSummary = PaymentToEcAmountSummary(
            amountsGroupedByPriority = expectedPaymentsIncludedInPaymentsToEc,
            totals = expectedTotal
        )

        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(PAYMENT_TO_EC_ID) } returns paymentApplicationDetail(
            status = PaymentEcStatus.Draft
        )
        every {
            ecPaymentLinkPersistence.calculateAndGetOverviewForDraftEcPayment(
                PAYMENT_TO_EC_ID
            )
        } returns paymentToEcAmountSummaryTmpMap

        assertThat(
            getCumulativeAmountsByType.getOverviewAmountsByType(
                paymentToEcId = PAYMENT_TO_EC_ID, type = null
            )
        ).isEqualTo(expectedSummary)
    }

    @Test
    fun `getCumulativeAmountsByType - ArtNot94Not95 - status finished`() {

        val expectedSummary = PaymentToEcAmountSummary(
            amountsGroupedByPriority = expectedPaymentsIncludedInPaymentsToEcFinished,
            totals = expectedTotalFinished,
        )
        val ecPaymentDetail = paymentApplicationDetail(status = PaymentEcStatus.Finished)


        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(PAYMENT_TO_EC_ID) } returns ecPaymentDetail
        every {
            ecPaymentLinkPersistence.getTotalsForFinishedEcPayment(
                PAYMENT_TO_EC_ID
            )
        } returns paymentsIncludedInPaymentsToEcMap

        assertThat(
            getCumulativeAmountsByType.getOverviewAmountsByType(
                paymentToEcId = PAYMENT_TO_EC_ID, type = PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95
            )
        ).isEqualTo(expectedSummary)

    }

    @Test
    fun `getCumulativeAmountsByType - summary - status finished`() {
        val expectedSummary = PaymentToEcAmountSummary(
            amountsGroupedByPriority = expectedPaymentsIncludedInPaymentsToEcFinished,
            totals = expectedTotalFinished,
        )
        val ecPaymentDetail = paymentApplicationDetail(status = PaymentEcStatus.Finished)

        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(PAYMENT_TO_EC_ID) } returns ecPaymentDetail
        every { ecPaymentLinkPersistence.getTotalsForFinishedEcPayment(PAYMENT_TO_EC_ID) } returns paymentsIncludedInPaymentsToEcMap

        assertThat(getCumulativeAmountsByType.getOverviewAmountsByType(PAYMENT_TO_EC_ID, type = null))
            .isEqualTo(expectedSummary)
    }
}
