package io.cloudflight.jems.server.payments.model.regular.toCreate

import java.math.BigDecimal

data class PaymentRegularToCreate(
    val projectId: Long,
    override val partnerPayments: List<PaymentPartnerToCreate>,
    override val amountApprovedPerFund: BigDecimal,

    override val defaultPartnerContribution: BigDecimal,
    override val defaultOfWhichPublic: BigDecimal,
    override val defaultOfWhichAutoPublic: BigDecimal,
    override val defaultOfWhichPrivate: BigDecimal,
) : PaymentToCreate
