package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource

data class FinancingSourceBreakdown(
    val sources: List<FinancingSourceBreakdownLine>,
    val total: FinancingSourceBreakdownLine,
)
