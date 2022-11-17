package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost

data class ExpenditureUnitCostBreakdown(
    val unitCosts: List<ExpenditureUnitCostBreakdownLine>,
    val total: ExpenditureUnitCostBreakdownLine,
)
