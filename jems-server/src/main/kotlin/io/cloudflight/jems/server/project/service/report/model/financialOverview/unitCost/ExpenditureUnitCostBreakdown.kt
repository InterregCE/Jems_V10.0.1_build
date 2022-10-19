package io.cloudflight.jems.server.project.service.report.model.financialOverview.unitCost

data class ExpenditureUnitCostBreakdown(
    val unitCosts: List<ExpenditureUnitCostBreakdownLine>,
    val total: ExpenditureUnitCostBreakdownLine,
)
