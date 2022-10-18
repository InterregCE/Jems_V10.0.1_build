package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

import java.math.BigDecimal

data class ExpenditureCoFinancingBreakdownLineDTO(
    val fundId: Long? = null,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyPaid: BigDecimal,
    val currentReport: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val remainingBudget: BigDecimal,
)
