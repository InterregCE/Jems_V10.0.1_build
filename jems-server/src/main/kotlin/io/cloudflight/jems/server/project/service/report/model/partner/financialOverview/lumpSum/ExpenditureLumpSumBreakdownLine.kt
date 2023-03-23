package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class ExpenditureLumpSumBreakdownLine(
    val reportLumpSumId: Long,
    val lumpSumId: Long,
    val name: Set<InputTranslation>,
    val period: Int?,

    override var totalEligibleBudget: BigDecimal,
    override var previouslyReported: BigDecimal,
    var previouslyReportedParked: BigDecimal,
    var previouslyPaid: BigDecimal,
    override var currentReport: BigDecimal,
    var currentReportReIncluded: BigDecimal,
    var totalEligibleAfterControl: BigDecimal,
    override var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    override var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    override var remainingBudget: BigDecimal = BigDecimal.ZERO,
) : BreakdownLine
