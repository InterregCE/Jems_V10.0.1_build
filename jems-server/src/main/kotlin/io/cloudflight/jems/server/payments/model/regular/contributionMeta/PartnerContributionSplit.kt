package io.cloudflight.jems.server.payments.model.regular.contributionMeta

import java.math.BigDecimal

data class PartnerContributionSplit(
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal
)
