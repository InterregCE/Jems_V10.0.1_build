package io.cloudflight.jems.server.project.service.report.model.project.base.create

import java.math.BigDecimal

data class ProjectReportUnitCostBase (
    val unitCostId: Long,
    var numberOfUnits: BigDecimal,
    var totalCost: BigDecimal,
    var previouslyReported: BigDecimal
)
