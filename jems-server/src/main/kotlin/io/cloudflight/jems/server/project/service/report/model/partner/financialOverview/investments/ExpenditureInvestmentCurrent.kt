package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments

import java.math.BigDecimal

data class ExpenditureInvestmentCurrent(
    val current: BigDecimal,
    val currentParked: BigDecimal
)
