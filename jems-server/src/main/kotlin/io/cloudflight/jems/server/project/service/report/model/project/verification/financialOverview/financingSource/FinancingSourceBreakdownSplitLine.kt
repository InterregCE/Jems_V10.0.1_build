package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource

import java.math.BigDecimal

data class FinancingSourceBreakdownSplitLine(
    val fundId: Long,
    val value: BigDecimal,

    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,

    val total: BigDecimal,
)
