package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

data class ExpenditureCostCategoryBreakdownDTO(
    val staff: ExpenditureCostCategoryBreakdownLineDTO,
    val office: ExpenditureCostCategoryBreakdownLineDTO,
    val travel: ExpenditureCostCategoryBreakdownLineDTO,
    val external: ExpenditureCostCategoryBreakdownLineDTO,
    val equipment: ExpenditureCostCategoryBreakdownLineDTO,
    val infrastructure: ExpenditureCostCategoryBreakdownLineDTO,
    val other: ExpenditureCostCategoryBreakdownLineDTO,
    val lumpSum: ExpenditureCostCategoryBreakdownLineDTO,
    val unitCost: ExpenditureCostCategoryBreakdownLineDTO,
    val total: ExpenditureCostCategoryBreakdownLineDTO,
)
