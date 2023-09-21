package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing

import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class CertificateCoFinancingBreakdownLine(
    val fundId: Long? = null,
    override val totalEligibleBudget: BigDecimal,

    override val previouslyReported: BigDecimal,
    override var currentReport: BigDecimal,

    var previouslyVerified: BigDecimal = BigDecimal.ZERO,
    var currentVerified: BigDecimal = BigDecimal.ZERO,

    override var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    override var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    override var remainingBudget: BigDecimal = BigDecimal.ZERO,
    val previouslyPaid: BigDecimal,
) : BreakdownLine
