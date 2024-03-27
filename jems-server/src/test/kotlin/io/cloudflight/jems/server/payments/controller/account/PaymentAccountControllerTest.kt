package io.cloudflight.jems.server.payments.controller.account

import io.cloudflight.jems.api.payments.dto.account.PaymentAccountDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountOverviewDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountOverviewDetailDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountStatusDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountUpdateDTO
import io.cloudflight.jems.api.payments.dto.account.finance.PaymentAccountAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.account.finance.PaymentAccountAmountSummaryLineDTO
import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewDetail
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.PaymentAccountUpdate
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.service.account.finalizePaymentAccount.FinalizePaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.finance.getAmountSummary.GetPaymentAccountAmountSummaryInteractor
import io.cloudflight.jems.server.payments.service.account.getPaymentAccount.GetPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.listPaymentAccount.ListPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.reOpenPaymentAccount.ReOpenPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.updatePaymentAccount.UpdatePaymentAccountInteractor
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class PaymentAccountControllerTest : UnitTest() {

    companion object {
        private const val FUND_ID = 11L
        private const val ACCOUNTING_YEAR_ID = 12L
        private const val PAYMENT_ACCOUNT_ID = 13L
        private val programmeFund = ProgrammeFund(id = FUND_ID, selected = true)
        private val startDate = LocalDate.now().minusDays(2)
        private val endDate = LocalDate.now().plusDays(2)
        private val submissionToSfcDate = LocalDate.now().plusDays(3)
        private val accountingYear =
            AccountingYear(id = ACCOUNTING_YEAR_ID, year = 2021, startDate = startDate, endDate = endDate)
        private val paymentAccount = PaymentAccount(
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
        private val paymentAccountOverviewDetail = PaymentAccountOverviewDetail(
            id = PAYMENT_ACCOUNT_ID,
            accountingYear = accountingYear,
            status = PaymentAccountStatus.DRAFT,
            totalEligibleExpenditure = BigDecimal.ZERO,
            nationalReference = "national reference",
            technicalAssistance = BigDecimal.ONE,
            totalPublicContribution = BigDecimal.ONE,
            totalClaimInclTA = BigDecimal.TEN,
            submissionToSfcDate = submissionToSfcDate,
            sfcNumber = "sfc number",
        )
        private val accountsOverviewList = listOf(
            PaymentAccountOverview(
                programmeFund = programmeFund,
                paymentAccounts = listOf(paymentAccountOverviewDetail)
            )
        )

        private val expectedProgrammeFund = ProgrammeFundDTO(id = FUND_ID, selected = true)
        private val expectedAccountingYear =
            AccountingYearDTO(id = ACCOUNTING_YEAR_ID, year = 2021, startDate = startDate, endDate = endDate)
        private val expectedPaymentAccount = PaymentAccountDTO(
            id = PAYMENT_ACCOUNT_ID,
            fund = expectedProgrammeFund,
            accountingYear = expectedAccountingYear,
            status = PaymentAccountStatusDTO.DRAFT,
            nationalReference = "national reference",
            technicalAssistance = BigDecimal.ONE,
            submissionToSfcDate = submissionToSfcDate,
            sfcNumber = "sfc number",
            comment = "comment"
        )
        private val expectedPaymentAccountOverviewDetail = PaymentAccountOverviewDetailDTO(
            id = PAYMENT_ACCOUNT_ID,
            accountingYear = expectedAccountingYear,
            status = PaymentAccountStatusDTO.DRAFT,
            totalEligibleExpenditure = BigDecimal.ZERO,
            nationalReference = "national reference",
            technicalAssistance = BigDecimal.ONE,
            totalPublicContribution = BigDecimal.ONE,
            totalClaimInclTA = BigDecimal.TEN,
            submissionToSfcDate = submissionToSfcDate,
            sfcNumber = "sfc number",
        )
        private val expectedAccountsOverviewList = listOf(
            PaymentAccountOverviewDTO(
                programmeFund = expectedProgrammeFund,
                paymentAccounts = listOf(expectedPaymentAccountOverviewDetail)
            )
        )
        private val paymentAccountUpdateDTO = PaymentAccountUpdateDTO(
            nationalReference = "national reference",
            technicalAssistance = BigDecimal.ONE,
            submissionToSfcDate = submissionToSfcDate,
            sfcNumber = "sfc number",
            comment = "comment"
        )
        private val paymentAccountUpdate = PaymentAccountUpdate(
            nationalReference = "national reference",
            technicalAssistance = BigDecimal.ONE,
            submissionToSfcDate = submissionToSfcDate,
            sfcNumber = "sfc number",
            comment = "comment"
        )


        private fun summaryLine() = PaymentAccountAmountSummaryLine(
            priorityAxis = "P01",
            totalEligibleExpenditure = BigDecimal.valueOf(20),
            totalPublicContribution = BigDecimal.valueOf(10)
        )

        private val paymentAccountCurrentOverviewSummary = PaymentAccountAmountSummary(
            amountsGroupedByPriority = listOf(summaryLine()),
            totals = summaryLine()
        )

        private fun summaryLineDTO() = PaymentAccountAmountSummaryLineDTO(
            priorityAxis = "P01",
            totalEligibleExpenditure = BigDecimal.valueOf(20),
            totalPublicContribution = BigDecimal.valueOf(10)
        )

        private val paymentAccountCurrentOverviewSummaryDTO = PaymentAccountAmountSummaryDTO(
            amountsGroupedByPriority = listOf(summaryLineDTO()),
            totals = summaryLineDTO()
        )
    }

    @MockK
    lateinit var getAllPaymentAccountsInteractor: ListPaymentAccountInteractor

    @MockK
    lateinit var getPaymentAccountInteractor: GetPaymentAccountInteractor

    @MockK
    lateinit var updatePaymentAccountInteractor: UpdatePaymentAccountInteractor

    @MockK
    lateinit var finalizePaymentAccountInteractor: FinalizePaymentAccountInteractor

    @MockK
    lateinit var reOpenPaymentAccountInteractor: ReOpenPaymentAccountInteractor

    @MockK
    lateinit var getPaymentAccountSummary: GetPaymentAccountAmountSummaryInteractor

    @InjectMockKs
    lateinit var controller: PaymentAccountController

    @Test
    fun listAccounts() {
        every { getAllPaymentAccountsInteractor.listPaymentAccount() } returns accountsOverviewList

        assertThat(controller.listPaymentAccount()).isEqualTo(expectedAccountsOverviewList)
    }

    @Test
    fun getPaymentAccount() {
        every { getPaymentAccountInteractor.getPaymentAccount(PAYMENT_ACCOUNT_ID) } returns paymentAccount

        assertThat(controller.getPaymentAccount(PAYMENT_ACCOUNT_ID))
            .isEqualTo(expectedPaymentAccount)
    }

    @Test
    fun updatePaymentAccount() {
        every { updatePaymentAccountInteractor.updatePaymentAccount(PAYMENT_ACCOUNT_ID, paymentAccountUpdate) } returns paymentAccount

        assertThat(controller.updatePaymentAccount(PAYMENT_ACCOUNT_ID, paymentAccountUpdateDTO))
            .isEqualTo(expectedPaymentAccount)
    }

    @Test
    fun finalizePaymentAccount() {
        every { finalizePaymentAccountInteractor.finalizePaymentAccount(PAYMENT_ACCOUNT_ID) } returns PaymentAccountStatus.FINISHED

        assertThat(controller.finalizePaymentAccount(PAYMENT_ACCOUNT_ID)).isEqualTo(PaymentAccountStatusDTO.FINISHED)
    }

    @Test
    fun reOpenPaymentAccount() {
        every { reOpenPaymentAccountInteractor.reOpenPaymentAccount(PAYMENT_ACCOUNT_ID) } returns PaymentAccountStatus.DRAFT

        assertThat(controller.reOpenPaymentAccount(PAYMENT_ACCOUNT_ID)).isEqualTo(PaymentAccountStatusDTO.DRAFT)
    }

    @Test
    fun getPaymentAccountAmountSummary() {
        every { getPaymentAccountSummary.getSummaryOverview(PAYMENT_ACCOUNT_ID) } returns paymentAccountCurrentOverviewSummary
        val summaryLineDTO = controller.getPaymentAccountAmountSummary(PAYMENT_ACCOUNT_ID)
        verify(exactly = 1) { getPaymentAccountSummary.getSummaryOverview(PAYMENT_ACCOUNT_ID)}
        assertThat(summaryLineDTO).isEqualTo(paymentAccountCurrentOverviewSummaryDTO)
    }

}
