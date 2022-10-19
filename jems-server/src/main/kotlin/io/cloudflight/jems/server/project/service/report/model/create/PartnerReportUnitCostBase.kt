package io.cloudflight.jems.server.project.service.report.model.create

import java.math.BigDecimal

data class PartnerReportUnitCostBase (
    val unitCostId: Long,
    var numberOfUnits: BigDecimal,
    var totalCost: BigDecimal,
    var previouslyReported: BigDecimal,
)
