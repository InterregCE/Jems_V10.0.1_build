package io.cloudflight.jems.server.project.service.report.model.create

import java.math.BigDecimal

data class PartnerReportLumpSum(
    val lumpSumId: Long,
    val period: Int?,
    val value: BigDecimal
)
