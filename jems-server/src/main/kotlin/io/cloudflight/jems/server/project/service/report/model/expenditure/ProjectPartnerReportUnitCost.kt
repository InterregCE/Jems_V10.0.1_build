package io.cloudflight.jems.server.project.service.report.model.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectPartnerReportUnitCost(
    val id: Long,
    val unitCostProgrammeId: Long,
    val costPerUnit: BigDecimal,
    val numberOfUnits: BigDecimal,
    val total: BigDecimal,
    val costPerUnitForeignCurrency: BigDecimal? = null,
    val foreignCurrencyCode: String? = null,
    val name: Set<InputTranslation> = emptySet(),
    val category: ReportBudgetCategory
)
