package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import java.math.BigDecimal

data class CertificateCostCategoryBreakdownLineDTO(
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val previouslyVerified: BigDecimal,
    val currentVerified: BigDecimal,

    val remainingBudget: BigDecimal,
)
