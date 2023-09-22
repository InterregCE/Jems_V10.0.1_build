package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal

data class CertificateUnitCostBreakdownLine(
    val reportUnitCostId: Long,
    val unitCostId: Long,
    val name: Set<InputTranslation>,

    override var totalEligibleBudget: BigDecimal,
    override var previouslyReported: BigDecimal,
    override var currentReport: BigDecimal,
    override var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    override var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    var previouslyVerified: BigDecimal,
    var currentVerified: BigDecimal,
    override var remainingBudget: BigDecimal = BigDecimal.ZERO,
) : BreakdownLine
