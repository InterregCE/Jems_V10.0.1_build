package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class ExpenditureInvestmentBreakdownLine(
    val reportInvestmentId: Long,
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,
    override var totalEligibleBudget: BigDecimal,
    override var previouslyReported: BigDecimal,
    var previouslyReportedParked: BigDecimal,
    override var currentReport: BigDecimal,
    var currentReportReIncluded: BigDecimal,
    var totalEligibleAfterControl: BigDecimal,
    override var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    override var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    override var remainingBudget: BigDecimal = BigDecimal.ZERO,
    var previouslyValidated: BigDecimal,
) : BreakdownLine
