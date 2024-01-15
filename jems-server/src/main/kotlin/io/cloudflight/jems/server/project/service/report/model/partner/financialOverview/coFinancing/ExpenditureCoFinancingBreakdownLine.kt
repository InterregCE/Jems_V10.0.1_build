package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class ExpenditureCoFinancingBreakdownLine(
    val fundId: Long? = null,
    override val totalEligibleBudget: BigDecimal,
    override val previouslyReported: BigDecimal,
    val previouslyReportedParked: BigDecimal,
    val previouslyReportedSpf: BigDecimal,
    override var currentReport: BigDecimal,
    var currentReportReIncluded: BigDecimal,
    val totalEligibleAfterControl: BigDecimal,
    override var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    override var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    override var remainingBudget: BigDecimal = BigDecimal.ZERO,
    val previouslyValidated: BigDecimal,
    val previouslyPaid: BigDecimal,

) : BreakdownLine
