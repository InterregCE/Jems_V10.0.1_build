package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewContribution
import io.cloudflight.jems.server.payments.repository.account.mapToOverviewAndFillInTotals
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.ecPayment.plusNullable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentAccountsListService(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val paymentAccountFinancePersistence: PaymentAccountFinancePersistence,
) {


    @Transactional(readOnly = true)
    fun listPaymentAccount(): List<PaymentAccountOverview> {
        val paymentAccountsByFund = paymentAccountPersistence.getAllAccounts().groupBy { it.fund }

        val linkedEcPaymentTotalsPerAccount = paymentAccountFinancePersistence.getEcPaymentTotalsForFinishedPaymentAccounts()
        val linkedCorrectionTotalsPerAccount = paymentAccountFinancePersistence.getCorrectionTotalsForFinishedPaymentAccounts()

        val totalsPerFinishedAccount = linkedEcPaymentTotalsPerAccount.mergeWith(linkedCorrectionTotalsPerAccount)

        return paymentAccountsByFund
            .mapToOverviewAndFillInTotals(totalsPerAccount = totalsPerFinishedAccount)
            .fillInTotalClaim()
    }

    private fun List<PaymentAccountOverview>.fillInTotalClaim() = onEach { fund ->
        fund.paymentAccounts.forEach {
            it.totalClaimInclTA = it.totalEligibleExpenditure.plus(it.technicalAssistance)
        }
    }

    private fun Map<Long, PaymentAccountOverviewContribution>.mergeWith(
        other: Map<Long, PaymentAccountOverviewContribution>
    ): Map<Long, PaymentAccountOverviewContribution> =
        (this.keys union other.keys).associateWith { id -> this[id].plus(other[id]) }

    private fun PaymentAccountOverviewContribution?.plus(other: PaymentAccountOverviewContribution?) =
        PaymentAccountOverviewContribution(
            totalEligibleExpenditure = this?.totalEligibleExpenditure.plusNullable(other?.totalEligibleExpenditure),
            totalPublicContribution = this?.totalPublicContribution.plusNullable(other?.totalPublicContribution)
        )

}
