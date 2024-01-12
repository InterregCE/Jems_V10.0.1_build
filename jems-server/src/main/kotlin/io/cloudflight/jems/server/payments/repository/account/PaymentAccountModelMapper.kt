package io.cloudflight.jems.server.payments.repository.account

import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.PaymentAccountEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewDetail
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal

fun List<PaymentAccountEntity>.toModel() = this.map { it.toModel() }

fun PaymentAccount.toOverviewDetailModel() = PaymentAccountOverviewDetail(
    id = id,
    accountingYear = accountingYear,
    status = status,
    totalEligibleExpenditure = BigDecimal.ZERO,
    nationalReference = nationalReference,
    technicalAssistance = technicalAssistance,
    totalPublicContribution = BigDecimal.ZERO,
    totalClaimInclTA = BigDecimal.ZERO,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = sfcNumber,
)

fun PaymentAccountEntity.toModel() = PaymentAccount(
    id = id,
    fund = programmeFund.toModel(),
    accountingYear = accountingYear.toModel(),
    status = status,
    nationalReference = nationalReference,
    technicalAssistance = technicalAssistance,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = sfcNumber,
    comment = comment
)

fun Map<ProgrammeFund, List<PaymentAccount>>.toOverviewModel() = map {
    PaymentAccountOverview(
        programmeFund = it.key,
        paymentAccounts = it.value.map{ account -> account.toOverviewDetailModel() }
    )
}

