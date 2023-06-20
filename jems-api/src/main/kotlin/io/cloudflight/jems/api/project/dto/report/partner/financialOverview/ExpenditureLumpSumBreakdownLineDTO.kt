package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ExpenditureLumpSumBreakdownLineDTO(
    val reportLumpSumId: Long,
    val lumpSumId: Long,
    val name: Set<InputTranslation>,
    val period: Int?,

    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    var previouslyReportedParked: BigDecimal,
    val previouslyPaid: BigDecimal,
    val currentReport: BigDecimal,
    var currentReportReIncluded: BigDecimal,
    val totalEligibleAfterControl: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val remainingBudget: BigDecimal,
    val previouslyValidated: BigDecimal,
)
