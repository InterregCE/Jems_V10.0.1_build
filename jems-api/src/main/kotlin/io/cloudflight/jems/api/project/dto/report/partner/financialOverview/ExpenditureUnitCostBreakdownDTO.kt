package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

data class ExpenditureUnitCostBreakdownDTO(
    val unitCosts: List<ExpenditureUnitCostBreakdownLineDTO>,
    val total: ExpenditureUnitCostBreakdownLineDTO,
)
