package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import java.math.BigDecimal

data class CertificateCoFinancingBreakdownLineDTO(
    val fundId: Long? = null,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val previouslyVerified: BigDecimal,
    val currentVerified: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val remainingBudget: BigDecimal,
    val previouslyPaid: BigDecimal,
)
