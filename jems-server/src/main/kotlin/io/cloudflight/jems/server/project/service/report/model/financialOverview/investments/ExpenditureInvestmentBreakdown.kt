package io.cloudflight.jems.server.project.service.report.model.financialOverview.investments

data class ExpenditureInvestmentBreakdown(
    val investments: List<ExpenditureInvestmentBreakdownLine>,
    val total: ExpenditureInvestmentBreakdownLine
)
