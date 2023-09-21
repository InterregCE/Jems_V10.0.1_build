package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class CertificateLumpSumBreakdownLineDTO(
    val reportLumpSumId: Long,
    val lumpSumId: Long,
    val name: Set<InputTranslation>,
    val period: Int?,
    val orderNr: Int,

    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyPaid: BigDecimal,
    val currentReport: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val previouslyVerified: BigDecimal,
    val currentVerified: BigDecimal,
    val remainingBudget: BigDecimal,
)
