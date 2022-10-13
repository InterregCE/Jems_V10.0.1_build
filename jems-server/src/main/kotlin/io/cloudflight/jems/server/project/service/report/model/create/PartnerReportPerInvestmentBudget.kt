package io.cloudflight.jems.server.project.service.report.model.create

import java.math.BigDecimal

data class PartnerReportPerInvestmentBudget(
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int?,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal
)
