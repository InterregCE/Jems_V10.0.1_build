package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing

import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class CertificateCoFinancingBreakdownLine(
    val fundId: Long? = null,
    override val totalEligibleBudget: BigDecimal,
    override val previouslyReported: BigDecimal,
    val previouslyPaid: BigDecimal,
    override var currentReport: BigDecimal,
    override var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    override var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    override var remainingBudget: BigDecimal = BigDecimal.ZERO,
) : BreakdownLine
