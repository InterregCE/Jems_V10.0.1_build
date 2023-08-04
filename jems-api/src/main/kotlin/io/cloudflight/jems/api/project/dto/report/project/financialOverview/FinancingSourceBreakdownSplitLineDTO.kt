package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import java.math.BigDecimal

data class FinancingSourceBreakdownSplitLineDTO(
    val fundId: Long,
    val value: BigDecimal,

    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,

    val total: BigDecimal,
)
