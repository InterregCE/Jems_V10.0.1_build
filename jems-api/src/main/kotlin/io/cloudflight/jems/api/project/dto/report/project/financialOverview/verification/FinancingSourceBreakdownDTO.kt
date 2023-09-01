package io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification

data class FinancingSourceBreakdownDTO(
    val sources: List<FinancingSourceBreakdownLineDTO>,
    val total: FinancingSourceBreakdownLineDTO,
)
