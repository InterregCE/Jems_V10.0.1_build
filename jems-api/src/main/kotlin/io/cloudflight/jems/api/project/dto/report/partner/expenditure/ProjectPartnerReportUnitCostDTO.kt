package io.cloudflight.jems.api.project.dto.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectPartnerReportUnitCostDTO(
    val id: Long,
    val unitCostProgrammeId: Long,
    val projectDefined: Boolean,
    val costPerUnit: BigDecimal,
    val total: BigDecimal,
    val numberOfUnits: BigDecimal,
    val costPerUnitForeignCurrency: BigDecimal? = null,
    val foreignCurrencyCode: String? = null,
    val category: BudgetCategoryDTO,
    val name: Set<InputTranslation> = emptySet(),
)
