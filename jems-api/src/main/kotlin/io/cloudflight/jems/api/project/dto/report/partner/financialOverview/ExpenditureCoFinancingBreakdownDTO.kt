package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

data class ExpenditureCoFinancingBreakdownDTO(
    val funds: List<ExpenditureCoFinancingBreakdownLineDTO>,
    val partnerContribution: ExpenditureCoFinancingBreakdownLineDTO,
    val publicContribution: ExpenditureCoFinancingBreakdownLineDTO,
    val automaticPublicContribution: ExpenditureCoFinancingBreakdownLineDTO,
    val privateContribution: ExpenditureCoFinancingBreakdownLineDTO,
    val total: ExpenditureCoFinancingBreakdownLineDTO,
)
