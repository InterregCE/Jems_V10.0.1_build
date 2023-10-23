package io.cloudflight.jems.server.payments.model.regular.toCreate

import java.math.BigDecimal

data class PaymentFtlsToCreate(
    val programmeLumpSumId: Long,
    override val partnerPayments: List<PaymentPartnerToCreate>,
    override val amountApprovedPerFund: BigDecimal,

    val projectCustomIdentifier: String,
    val projectAcronym: String,

    override val defaultPartnerContribution: BigDecimal,
    override val defaultOfWhichPublic: BigDecimal,
    override val defaultOfWhichAutoPublic: BigDecimal,
    override val defaultOfWhichPrivate: BigDecimal,
) : PaymentToCreate
