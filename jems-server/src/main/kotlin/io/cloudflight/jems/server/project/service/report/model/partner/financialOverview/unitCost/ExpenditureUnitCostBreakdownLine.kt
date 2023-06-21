package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class ExpenditureUnitCostBreakdownLine(
    val reportUnitCostId: Long,
    val unitCostId: Long,
    val name: Set<InputTranslation>,

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
