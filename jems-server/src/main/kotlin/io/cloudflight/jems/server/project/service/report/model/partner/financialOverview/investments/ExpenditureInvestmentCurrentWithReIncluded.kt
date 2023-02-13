package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments

import java.math.BigDecimal

data class ExpenditureInvestmentCurrentWithReIncluded(
    val current: BigDecimal,
    val currentReIncluded: BigDecimal
)
