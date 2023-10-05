package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getCumulativeAmountsByType

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getCumulativeAmountsByType.GetCumulativeAmountsByType
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
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

        private val paymentsIncludedInPaymentsToEc = listOf(
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
            ),
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

        private val paymentsIncludedInPaymentsToEcMap =
            mapOf(Pair(PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95, paymentsIncludedInPaymentsToEc))

        private val paymentsIncludedInPaymentsToEcTmp = listOf(
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

        private val expectedTotal = PaymentToEcAmountSummaryLine(
            priorityAxis = null,
            totalEligibleExpenditure = BigDecimal(606),
            totalUnionContribution = BigDecimal.ZERO,
            totalPublicContribution = BigDecimal(1609)
        )

        private val paymentToEcAmountSummaryTmpMap = mapOf(
            Pair(
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                paymentsIncludedInPaymentsToEcTmp
            )
        )
    }

    @MockK
    lateinit var paymentApplicationToEcPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @InjectMockKs
    lateinit var getCumulativeAmountsByType: GetCumulativeAmountsByType

    @Test
    fun `getCumulativeAmountsByType - notArt9495`() {
       val expectedSummary = PaymentToEcAmountSummary(
            amountsGroupedByPriority = expectedPaymentsIncludedInPaymentsToEc,
            totals = expectedTotal
        )

        every { paymentApplicationToEcPersistence.getPaymentApplicationToEcDetail(PAYMENT_TO_EC_ID) } returns paymentApplicationDetail(
            status = PaymentEcStatus.Draft
        )
        every {
            paymentApplicationToEcPersistence.getSelectedPaymentsToEcPayment(
                PAYMENT_TO_EC_ID
            )
        } returns paymentToEcAmountSummaryTmpMap

        assertThat(
            getCumulativeAmountsByType.getCumulativeAmountsByType(
                paymentToEcId = PAYMENT_TO_EC_ID, type = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
            )
        ).isEqualTo(expectedSummary)
    }

    @Test
    fun `getCumulativeAmountsByType - summary`() {
        val expectedSummary = PaymentToEcAmountSummary(
            amountsGroupedByPriority = expectedPaymentsIncludedInPaymentsToEc,
            totals = expectedTotal
        )

        every { paymentApplicationToEcPersistence.getPaymentApplicationToEcDetail(PAYMENT_TO_EC_ID) } returns paymentApplicationDetail(
            status = PaymentEcStatus.Draft
        )
        every {
            paymentApplicationToEcPersistence.getSelectedPaymentsToEcPayment(
                PAYMENT_TO_EC_ID
            )
        } returns paymentToEcAmountSummaryTmpMap

        assertThat(
            getCumulativeAmountsByType.getCumulativeAmountsByType(
                paymentToEcId = PAYMENT_TO_EC_ID, type = null
            )
        ).isEqualTo(expectedSummary)
    }

    @Test
    fun `getCumulativeAmountsByType - ArtNot94Not95 - status finished`() {

        val expectedSummary = PaymentToEcAmountSummary(
            amountsGroupedByPriority = expectedPaymentsIncludedInPaymentsToEc,
            totals = expectedTotal
        )
        val ecPaymentDetail = paymentApplicationDetail(status = PaymentEcStatus.Finished)


        every { paymentApplicationToEcPersistence.getPaymentApplicationToEcDetail(PAYMENT_TO_EC_ID) } returns ecPaymentDetail
        every {
            paymentApplicationToEcPersistence.getSavedCumulativeAmountsForPaymentsToEcByType(
                PAYMENT_TO_EC_ID
            )
        } returns paymentsIncludedInPaymentsToEcMap

        assertThat(
            getCumulativeAmountsByType.getCumulativeAmountsByType(
                paymentToEcId = PAYMENT_TO_EC_ID, type = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
            )
        ).isEqualTo(expectedSummary)

    }

    @Test
    fun `getCumulativeAmountsByType - summary - status finished`() {

        val expectedSummary = PaymentToEcAmountSummary(
            amountsGroupedByPriority = expectedPaymentsIncludedInPaymentsToEc,
            totals = expectedTotal
        )
        val ecPaymentDetail = paymentApplicationDetail(status = PaymentEcStatus.Finished)

        every { paymentApplicationToEcPersistence.getPaymentApplicationToEcDetail(PAYMENT_TO_EC_ID) } returns ecPaymentDetail
        every {
            paymentApplicationToEcPersistence.getSavedCumulativeAmountsForPaymentsToEcByType(
                PAYMENT_TO_EC_ID
            )
        } returns paymentsIncludedInPaymentsToEcMap

        assertThat(
            getCumulativeAmountsByType.getCumulativeAmountsByType(
                paymentToEcId = PAYMENT_TO_EC_ID, type = null
            )
        ).isEqualTo(expectedSummary)
    }
}
