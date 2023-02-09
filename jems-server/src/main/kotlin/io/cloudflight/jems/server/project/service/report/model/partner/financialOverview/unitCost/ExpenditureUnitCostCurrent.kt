package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost

import java.math.BigDecimal

data class ExpenditureUnitCostCurrent(
    val current: BigDecimal,
    val currentParked: BigDecimal,
)
