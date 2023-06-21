package io.cloudflight.jems.server.project.service.report.model.project.base.create

import java.math.BigDecimal

data class PreviouslyProjectReportedFund(
    val fundId: Long?,
    val percentage: BigDecimal,
    val total: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyPaid: BigDecimal,
)
