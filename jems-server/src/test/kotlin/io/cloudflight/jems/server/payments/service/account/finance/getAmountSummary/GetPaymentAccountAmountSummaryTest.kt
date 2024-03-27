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

        val summary =  PaymentAccountAmountSummary(
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
    }



    @MockK
    lateinit var paymentAccountAmountSummaryService: PaymentAccountAmountSummaryService

    @InjectMockKs
    lateinit var interactor: GetPaymentAccountAmountSummary

    @Test
    fun getSummaryOverview() {
        every { paymentAccountAmountSummaryService.getSummaryOverview(PAYMENT_ACCOUNT_ID) } returns summary

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
