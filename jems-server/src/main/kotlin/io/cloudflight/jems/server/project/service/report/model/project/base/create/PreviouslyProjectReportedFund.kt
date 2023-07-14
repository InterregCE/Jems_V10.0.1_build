package io.cloudflight.jems.server.project.service.report.model.project.base.create

import java.math.BigDecimal

data class PreviouslyProjectReportedFund(
    val fundId: Long?,
    val total: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyVerified: BigDecimal,
    val previouslyPaid: BigDecimal,
)
