package io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing

data class ExpenditureCoFinancingBreakdown(
    val funds: List<ExpenditureCoFinancingBreakdownLine>,
    val partnerContribution: ExpenditureCoFinancingBreakdownLine,
    val publicContribution: ExpenditureCoFinancingBreakdownLine,
    val automaticPublicContribution: ExpenditureCoFinancingBreakdownLine,
    val privateContribution: ExpenditureCoFinancingBreakdownLine,
    val total: ExpenditureCoFinancingBreakdownLine,
)
