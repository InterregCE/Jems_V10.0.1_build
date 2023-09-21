package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class CertificateUnitCostBreakdownLineDTO(
    val reportUnitCostId: Long,
    val unitCostId: Long,
    val name: Set<InputTranslation>,

    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val previouslyVerified: BigDecimal,
    val currentVerified: BigDecimal,
    val remainingBudget: BigDecimal,
)
