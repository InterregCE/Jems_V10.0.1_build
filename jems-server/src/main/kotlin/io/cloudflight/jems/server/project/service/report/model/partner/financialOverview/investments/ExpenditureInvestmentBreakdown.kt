package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments

data class ExpenditureInvestmentBreakdown(
    val investments: List<ExpenditureInvestmentBreakdownLine>,
    val total: ExpenditureInvestmentBreakdownLine
)
