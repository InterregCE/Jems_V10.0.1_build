package io.cloudflight.jems.server.payments.model.regular.contributionMeta

import java.math.BigDecimal

data class ContributionMeta(
    val projectId: Long,
    val partnerId: Long,

    // lump sum identifier combined as:
    val programmeLumpSumId: Long,
    val orderNr: Int,

    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal
)
