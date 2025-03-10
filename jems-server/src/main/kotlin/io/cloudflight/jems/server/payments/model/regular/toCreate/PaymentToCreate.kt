package io.cloudflight.jems.server.payments.model.regular.toCreate

import java.math.BigDecimal

interface PaymentToCreate {
    val partnerPayments: List<PaymentPartnerToCreate>
    val amountApprovedPerFund: BigDecimal

    val defaultTotalEligibleWithoutSco: BigDecimal
    val defaultFundAmountUnionContribution: BigDecimal
    val defaultFundAmountPublicContribution: BigDecimal
    val defaultPartnerContribution: BigDecimal
    val defaultOfWhichPublic: BigDecimal
    val defaultOfWhichAutoPublic: BigDecimal
    val defaultOfWhichPrivate: BigDecimal
}
