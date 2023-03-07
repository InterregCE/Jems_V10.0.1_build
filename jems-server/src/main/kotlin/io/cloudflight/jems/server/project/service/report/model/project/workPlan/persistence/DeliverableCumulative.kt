package io.cloudflight.jems.server.project.service.report.model.project.workPlan.persistence

import java.math.BigDecimal

data class DeliverableCumulative(
    val wpNumber: Int,
    val activityNumber: Int,
    val deliverableNumber: Int,
    val cumulative: BigDecimal,
)
