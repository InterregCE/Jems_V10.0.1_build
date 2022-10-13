package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

data class ExpenditureInvestmentBreakdownDTO(
    val investments: List<ExpenditureInvestmentBreakdownLineDTO>,
    val total: ExpenditureInvestmentBreakdownLineDTO
)
