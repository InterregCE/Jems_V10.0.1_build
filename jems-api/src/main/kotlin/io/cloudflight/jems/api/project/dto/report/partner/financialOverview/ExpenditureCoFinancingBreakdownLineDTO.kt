package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

import java.math.BigDecimal

data class ExpenditureCoFinancingBreakdownLineDTO(
    val fundId: Long?,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyReportedParked: BigDecimal,
    val currentReport: BigDecimal,
    val currentReportReIncluded: BigDecimal,
    val totalEligibleAfterControl: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val remainingBudget: BigDecimal,
    val previouslyValidated: BigDecimal,
    val previouslyPaid: BigDecimal,
)
