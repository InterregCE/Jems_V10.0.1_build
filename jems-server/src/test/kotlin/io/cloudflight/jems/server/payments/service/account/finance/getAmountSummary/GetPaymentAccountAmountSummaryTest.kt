package io.cloudflight.jems.server.payments.service.account.finance.getAmountSummary

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.service.account.ACCOUNTING_YEAR_ID
import io.cloudflight.jems.server.payments.service.account.FUND_ID
import io.cloudflight.jems.server.payments.service.account.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.accountingYear
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.payments.service.account.programmeFund
import io.cloudflight.jems.server.payments.service.account.submissionToSfcDate
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetPaymentAccountAmountSummaryTest : UnitTest() {

    companion object {
        val paymentAccount = PaymentAccount(
            id = PAYMENT_ACCOUNT_ID,
            fund = programmeFund,
            accountingYear = accountingYear,
            status = PaymentAccountStatus.DRAFT,
            nationalReference = "national reference",
            technicalAssistance = BigDecimal.ONE,
            submissionToSfcDate = submissionToSfcDate,
            sfcNumber = "sfc number",
            comment = "comment"
        )

        val totalsForFinishedEcPayments = mapOf<Long?, PaymentToEcAmountSummaryLine>(
            11L to PaymentToEcAmountSummaryLine(
                priorityAxis = "P01",
                totalEligibleExpenditure = BigDecimal(101),
                totalUnionContribution = BigDecimal(10),
                totalPublicContribution = BigDecimal(102),
            ),
            22L to PaymentToEcAmountSummaryLine(
                priorityAxis = "P02",
                totalEligibleExpenditure = BigDecimal(201),
                totalUnionContribution = BigDecimal(20),
                totalPublicContribution = BigDecimal(202),
            ),
            33L to PaymentToEcAmountSummaryLine(
                priorityAxis = "P03",
                totalEligibleExpenditure = BigDecimal(301),
                totalUnionContribution = BigDecimal(30),
                totalPublicContribution = BigDecimal(302),
            ),
        )

        val totalsForDraftEcPayment = mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
                44L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 44L,
                    priorityAxis = "P04",
                    fundAmount = BigDecimal(401),
                    correctedFundAmount = BigDecimal(400),
                    partnerContribution = BigDecimal(202),
                    ofWhichPublic = BigDecimal(100),
                    ofWhichAutoPublic = BigDecimal(101),
                    unionContribution = BigDecimal(102),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal(400)
                )
            ),
            PaymentToEcOverviewType.FallsUnderArticle94Or95 to mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
                55L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 55L,
                    priorityAxis = "P05",
                    fundAmount = BigDecimal(501),
                    correctedFundAmount = BigDecimal(500),
                    partnerContribution = BigDecimal(303),
                    ofWhichPublic = BigDecimal(100),
                    ofWhichAutoPublic = BigDecimal(101),
                    unionContribution = BigDecimal(102),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal(500)
                )
            ),
            PaymentToEcOverviewType.Correction to mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
                66L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 66L,
                    priorityAxis = "P06",
                    fundAmount = BigDecimal(601),
                    correctedFundAmount = BigDecimal(600),
                    partnerContribution = BigDecimal(404),
                    ofWhichPublic = BigDecimal(100),
                    ofWhichAutoPublic = BigDecimal(101),
                    unionContribution = BigDecimal(102),
                    correctedTotalEligibleWithoutArt94Or95 = BigDecimal(600)
                )
            )
        )

        val accountOverview = mapOf<Long?, PaymentAccountAmountSummaryLineTmp>(
            77L to PaymentAccountAmountSummaryLineTmp(
                77L,
                "P07",
                BigDecimal(77),
                BigDecimal(70),
                BigDecimal(20),
                BigDecimal(10)
            ),
            88L to PaymentAccountAmountSummaryLineTmp(
                88L,
                "P08",
                BigDecimal(88),
                BigDecimal(80),
                BigDecimal(30),
                BigDecimal(20)
            ),
            99L to PaymentAccountAmountSummaryLineTmp(
                99L,
                "P09",
                BigDecimal(99),
                BigDecimal(90),
                BigDecimal(40),
                BigDecimal(30)
            ),
        )
    }

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var paymentAccountFinancePersistence: PaymentAccountFinancePersistence

    @MockK
    lateinit var paymentAccountCorrectionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @MockK
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var ecPaymentCorrectionLinkingPersistence: PaymentApplicationToEcLinkPersistence

    @InjectMockKs
    lateinit var interactor: GetPaymentAccountAmountSummary

    @Test
    fun getSummaryOverview() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount

        every { ecPaymentPersistence.getFinishedIdsByFundAndAccountingYear(FUND_ID, ACCOUNTING_YEAR_ID) } returns setOf(10L, 20L, 30L)
        every { paymentAccountFinancePersistence.getTotalsForFinishedEcPayments(setOf(10L, 20L, 30L)) } returns totalsForFinishedEcPayments

        every { ecPaymentPersistence.getDraftIdsByFundAndAccountingYear(FUND_ID, ACCOUNTING_YEAR_ID) } returns setOf(40L)
        every { ecPaymentCorrectionLinkingPersistence.calculateAndGetOverviewForDraftEcPayment(40L) } returns totalsForDraftEcPayment

        every { paymentAccountCorrectionLinkingPersistence.calculateOverviewForDraftPaymentAccount(PAYMENT_ACCOUNT_ID) } returns accountOverview

        assertThat(interactor.getSummaryOverview(PAYMENT_ACCOUNT_ID)).isEqualTo(
            PaymentAccountAmountSummary(
                amountsGroupedByPriority = listOf(
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P01",
                        totalEligibleExpenditure = BigDecimal(111L),
                        totalPublicContribution = BigDecimal(102L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P02",
                        totalEligibleExpenditure = BigDecimal(221L),
                        totalPublicContribution = BigDecimal(202L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P03",
                        totalEligibleExpenditure = BigDecimal(331L),
                        totalPublicContribution = BigDecimal(302L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P04",
                        totalEligibleExpenditure = BigDecimal(603L),
                        totalPublicContribution = BigDecimal(602L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P05",
                        totalEligibleExpenditure = BigDecimal(602L),
                        totalPublicContribution = BigDecimal(701L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P06",
                        totalEligibleExpenditure = BigDecimal(702L),
                        totalPublicContribution = BigDecimal(801L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P07",
                        totalEligibleExpenditure = BigDecimal(147L),
                        totalPublicContribution = BigDecimal(107L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P08",
                        totalEligibleExpenditure = BigDecimal(168L),
                        totalPublicContribution = BigDecimal(138L),
                    ),
                    PaymentAccountAmountSummaryLine(
                        priorityAxis = "P09",
                        totalEligibleExpenditure = BigDecimal(189L),
                        totalPublicContribution = BigDecimal(169L),
                    ),
                ),
                totals = PaymentAccountAmountSummaryLine(
                    priorityAxis = null,
                    totalEligibleExpenditure = BigDecimal(3074L),
                    totalPublicContribution = BigDecimal(3124L),
                ),
            )
        )
    }

}
