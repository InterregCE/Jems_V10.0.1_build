package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ExpenditureUnitCostBreakdownLineDTO(
    val reportUnitCostId: Long,
    val unitCostId: Long,
    val name: Set<InputTranslation>,

    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val totalEligibleAfterControl: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val remainingBudget: BigDecimal,
)
