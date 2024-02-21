package io.cloudflight.jems.server.payments.repository.account.finance

import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewContribution
import io.cloudflight.jems.server.payments.service.ecPayment.plusNullable

fun Map<Long, PaymentAccountOverviewContribution>.mergeWith(
    other: Map<Long, PaymentAccountOverviewContribution>
): Map<Long, PaymentAccountOverviewContribution> {
    val ids = this.keys union other.keys
    return ids.associateWith { id -> this[id].plus(other[id]) }
}

fun PaymentAccountOverviewContribution?.plus(other: PaymentAccountOverviewContribution?) =
    PaymentAccountOverviewContribution(
        totalEligibleExpenditure = this?.totalEligibleExpenditure.plusNullable(other?.totalEligibleExpenditure),
        totalPublicContribution = this?.totalPublicContribution.plusNullable(other?.totalPublicContribution)
    )
