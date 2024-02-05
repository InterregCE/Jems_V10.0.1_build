package io.cloudflight.jems.server.payments.service.account

import io.cloudflight.jems.server.payments.accountingYears.repository.toEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewDetail
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.PaymentAccountUpdate
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.programme.repository.fund.toEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal
import java.time.LocalDate

const val FUND_ID = 11L
const val ACCOUNTING_YEAR_ID = 12L
const val PAYMENT_ACCOUNT_ID = 13L
val programmeFund = ProgrammeFund(id = FUND_ID, selected = true)
val startDate = LocalDate.now().minusDays(2)
val endDate = LocalDate.now().plusDays(2)
val submissionToSfcDate = LocalDate.now().plusDays(3)
val submissionToSfcDateUpdated = LocalDate.now().plusDays(4)
val accountingYear =
    AccountingYear(id = ACCOUNTING_YEAR_ID, year = 2021, startDate = startDate, endDate = endDate)

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

val paymentAccountUpdated = PaymentAccount(
    id = PAYMENT_ACCOUNT_ID,
    fund = programmeFund,
    accountingYear = accountingYear,
    status = PaymentAccountStatus.DRAFT,
    nationalReference = "national reference updated",
    technicalAssistance = BigDecimal.TEN,
    submissionToSfcDate = submissionToSfcDateUpdated,
    sfcNumber = "sfc number updated",
    comment = "comment updated"
)

val expectedProgrammeFund = ProgrammeFund(id = FUND_ID, selected = true)

val expectedAccountingYear =
    AccountingYear(id = ACCOUNTING_YEAR_ID, year = 2021, startDate = startDate, endDate = endDate)

val expectedPaymentAccountOverviewDetail = PaymentAccountOverviewDetail(
    id = PAYMENT_ACCOUNT_ID,
    accountingYear = expectedAccountingYear,
    status = PaymentAccountStatus.DRAFT,
    totalEligibleExpenditure = BigDecimal.ZERO,
    nationalReference = "national reference",
    technicalAssistance = BigDecimal.ONE,
    totalPublicContribution = BigDecimal.ZERO,
    totalClaimInclTA = BigDecimal.ZERO,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = "sfc number",
)

val expectedAccountsOverviewList = listOf(
    PaymentAccountOverview(
        programmeFund = expectedProgrammeFund,
        paymentAccounts = listOf(expectedPaymentAccountOverviewDetail)
    )
)

val paymentAccountUpdate = PaymentAccountUpdate(
    nationalReference = "national reference updated",
    technicalAssistance = BigDecimal.TEN,
    submissionToSfcDate = submissionToSfcDateUpdated,
    sfcNumber = "sfc number updated",
    comment = "comment updated"
)

val expectedPaymentAccountUpdate = PaymentAccount(
    id = PAYMENT_ACCOUNT_ID,
    fund = programmeFund,
    accountingYear = accountingYear,
    status = PaymentAccountStatus.DRAFT,
    nationalReference = "national reference updated",
    technicalAssistance = BigDecimal.TEN,
    submissionToSfcDate = submissionToSfcDateUpdated,
    sfcNumber = "sfc number updated",
    comment = "comment updated"
)

val paymentAccountUpdateWithErrors = PaymentAccountUpdate(
    nationalReference = "national reference",
    technicalAssistance = BigDecimal.ONE,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = "sfc number".repeat(30),
    comment = "comment".repeat(5000)
)

fun paymentAccountEntity() = PaymentAccountEntity(
    id = PAYMENT_ACCOUNT_ID,
    programmeFund = programmeFund.toEntity(),
    accountingYear = accountingYear.toEntity(),
    nationalReference = "national reference",
    technicalAssistance = BigDecimal.ONE,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = "sfc number",
    comment = "comment",
    status = PaymentAccountStatus.DRAFT
)
