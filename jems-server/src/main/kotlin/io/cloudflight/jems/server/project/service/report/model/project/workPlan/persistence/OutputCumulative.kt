package io.cloudflight.jems.server.project.service.report.model.project.workPlan.persistence

import java.math.BigDecimal

data class OutputCumulative(
    val wpNumber: Int,
    val outputNumber: Int,
    val cumulative: BigDecimal,
)
