package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

import java.math.BigDecimal

data class ExpenditureCostCategoryBreakdownLineDTO(
    val flatRate: Int?,

    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    var previouslyReportedParked: BigDecimal,
    val currentReport: BigDecimal,
    var currentReportReIncluded: BigDecimal,
    val totalEligibleAfterControl: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val remainingBudget: BigDecimal,
)
