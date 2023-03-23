package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class CertificateLumpSumBreakdownLine(
    val reportLumpSumId: Long,
    val lumpSumId: Long,
    val name: Set<InputTranslation>,
    val period: Int?,
    val orderNr: Int,

    override var totalEligibleBudget: BigDecimal,
    override var previouslyReported: BigDecimal,
    var previouslyPaid: BigDecimal,
    override var currentReport: BigDecimal,
    override var totalReportedSoFar: BigDecimal,
    override var totalReportedSoFarPercentage: BigDecimal,
    override var remainingBudget: BigDecimal,
) : BreakdownLine
