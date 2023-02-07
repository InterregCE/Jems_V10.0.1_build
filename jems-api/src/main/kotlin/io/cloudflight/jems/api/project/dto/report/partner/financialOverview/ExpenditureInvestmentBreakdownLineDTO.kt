package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ExpenditureInvestmentBreakdownLineDTO(
    val reportInvestmentId: Long,
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyReportedParked: BigDecimal,
    val currentReport: BigDecimal,
    val currentReportReIncluded: BigDecimal,
    val totalEligibleAfterControl: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage : BigDecimal,
    val remainingBudget: BigDecimal
)
