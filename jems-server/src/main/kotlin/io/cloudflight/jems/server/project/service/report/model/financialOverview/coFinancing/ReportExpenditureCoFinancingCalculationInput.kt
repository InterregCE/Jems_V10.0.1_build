package io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing

import java.math.BigDecimal

data class ReportExpenditureCoFinancingCalculationInput(
    val currentTotal: BigDecimal,
    val fundsPercentages: Map<Long, BigDecimal>,
    val partnerContributionPercentage: BigDecimal,
    val publicPercentage: BigDecimal,
    val automaticPublicPercentage: BigDecimal,
    val privatePercentage: BigDecimal,
)
