package io.cloudflight.jems.server.project.repository.report.project.coFinancing

import java.math.BigDecimal

data class ProjectReportCumulativeFund(
    val reportFundId: Long?,
    val sum: BigDecimal,
)
