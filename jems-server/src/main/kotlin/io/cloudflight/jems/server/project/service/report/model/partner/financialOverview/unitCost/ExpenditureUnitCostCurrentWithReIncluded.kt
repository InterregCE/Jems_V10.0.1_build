package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost

import java.math.BigDecimal

data class ExpenditureUnitCostCurrentWithReIncluded(
    val current: BigDecimal,
    val currentReIncluded: BigDecimal,
)
