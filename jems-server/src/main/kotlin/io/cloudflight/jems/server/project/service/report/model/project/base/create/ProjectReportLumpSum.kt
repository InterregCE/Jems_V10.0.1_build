package io.cloudflight.jems.server.project.service.report.model.project.base.create

import java.math.BigDecimal

data class ProjectReportLumpSum(
    val lumpSumId: Long,
    val orderNr: Int,
    val period: Int?,
    val total: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyPaid: BigDecimal,
)
