package io.cloudflight.jems.server.project.service.report.model.create

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import java.math.BigDecimal

data class PartnerReportUnitCost(
    val unitCostId: Long,
    var totalCost: BigDecimal,
    var numberOfUnits: BigDecimal,
    val costPerUnit: BigDecimal,
    val costPerUnitForeignCurrency: BigDecimal? = null,
    val foreignCurrencyCode: String? = null,
    val categories: Set<BudgetCategory> = emptySet()
)
