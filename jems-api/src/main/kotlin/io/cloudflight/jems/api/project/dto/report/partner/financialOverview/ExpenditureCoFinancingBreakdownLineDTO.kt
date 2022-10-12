package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

import java.math.BigDecimal

data class ExpenditureCoFinancingBreakdownLineDTO(
    val fundId: Long? = null,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyPaid: BigDecimal,
    var currentReport: BigDecimal,
    var totalReportedSoFar: BigDecimal,
    var totalReportedSoFarPercentage: BigDecimal,
    var remainingBudget: BigDecimal,
)
