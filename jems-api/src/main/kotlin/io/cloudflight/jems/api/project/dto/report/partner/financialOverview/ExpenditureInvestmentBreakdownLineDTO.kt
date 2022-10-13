package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

import java.math.BigDecimal

data class ExpenditureInvestmentBreakdownLineDTO(
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage : BigDecimal,
    val remainingBudget: BigDecimal
)
