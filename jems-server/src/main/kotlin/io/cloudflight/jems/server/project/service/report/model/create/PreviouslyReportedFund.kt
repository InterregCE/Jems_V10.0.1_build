package io.cloudflight.jems.server.project.service.report.model.create

import java.math.BigDecimal

data class PreviouslyReportedFund(
    val fundId: Long?,
    val percentage: BigDecimal,
    val total: BigDecimal,
    val previouslyReported: BigDecimal,
)
