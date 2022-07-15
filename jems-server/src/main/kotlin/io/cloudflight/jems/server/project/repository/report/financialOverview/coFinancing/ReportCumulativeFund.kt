package io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing

import java.math.BigDecimal

data class ReportCumulativeFund(
    val reportFundId: Long?,
    val sum: BigDecimal,
)
