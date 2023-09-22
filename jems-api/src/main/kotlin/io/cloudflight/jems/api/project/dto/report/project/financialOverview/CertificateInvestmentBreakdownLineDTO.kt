package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class CertificateInvestmentBreakdownLineDTO(
    val reportInvestmentId: Long,
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage : BigDecimal,
    val previouslyVerified: BigDecimal,
    val currentVerified: BigDecimal,
    val remainingBudget: BigDecimal
)
