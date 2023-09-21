package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory

import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class CertificateCostCategoryBreakdownLine(
    override val totalEligibleBudget: BigDecimal,
    override val previouslyReported: BigDecimal,
    override var currentReport: BigDecimal,
    override var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    override var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    override var remainingBudget: BigDecimal = BigDecimal.ZERO,
    var currentVerified: BigDecimal = BigDecimal.ZERO,
    var previouslyVerified: BigDecimal = BigDecimal.ZERO,
) : BreakdownLine
