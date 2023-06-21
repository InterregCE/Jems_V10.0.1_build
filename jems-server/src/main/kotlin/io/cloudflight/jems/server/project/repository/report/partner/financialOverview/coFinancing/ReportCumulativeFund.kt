package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import java.math.BigDecimal

data class ReportCumulativeFund(
    val reportFundId: Long?,
    val currentSum: BigDecimal,
    val currentParkedSum: BigDecimal,
)
