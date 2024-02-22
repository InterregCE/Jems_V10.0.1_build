package io.cloudflight.jems.server.payments.repository.account

import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewContribution
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewDetail
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal

fun List<PaymentAccountEntity>.toModel() = this.map { it.toModel() }

private fun PaymentAccount.toOverviewDetailModel(overviewTotals: Map<Long, PaymentAccountOverviewContribution>) = PaymentAccountOverviewDetail(
    id = id,
    accountingYear = accountingYear,
    status = status,
    totalEligibleExpenditure = overviewTotals[id]?.totalEligibleExpenditure ?: BigDecimal.ZERO,
    nationalReference = nationalReference,
    technicalAssistance = technicalAssistance,
    totalPublicContribution = overviewTotals[id]?.totalPublicContribution ?: BigDecimal.ZERO,
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

fun Map<ProgrammeFund, List<PaymentAccount>>.mapToOverviewAndFillInTotals(totalsPerAccount: Map<Long, PaymentAccountOverviewContribution>) =
    map {
        PaymentAccountOverview(
            programmeFund = it.key,
            paymentAccounts = it.value.map { account -> account.toOverviewDetailModel(totalsPerAccount) }
        )
    }

