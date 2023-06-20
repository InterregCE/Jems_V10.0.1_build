package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory

import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class ExpenditureCostCategoryBreakdownLine(
    val flatRate: Int?,

    override val totalEligibleBudget: BigDecimal,
    override val previouslyReported: BigDecimal,
    var previouslyReportedParked: BigDecimal,
    override var currentReport: BigDecimal,
    var currentReportReIncluded: BigDecimal,
    val totalEligibleAfterControl: BigDecimal,
    override var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    override var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    override var remainingBudget: BigDecimal = BigDecimal.ZERO,
    val previouslyValidated: BigDecimal,
) : BreakdownLine
