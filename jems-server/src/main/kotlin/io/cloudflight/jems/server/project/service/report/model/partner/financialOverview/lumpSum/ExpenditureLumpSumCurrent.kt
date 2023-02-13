package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum

import java.math.BigDecimal

data class ExpenditureLumpSumCurrent(
    val current: BigDecimal,
    val currentParked: BigDecimal,
)
