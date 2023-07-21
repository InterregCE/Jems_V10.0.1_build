package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing

import java.math.BigDecimal

data class PaymentCumulativeData(
    val amounts: PaymentCumulativeAmounts,
    val confirmedAndPaid: Map<Long, BigDecimal>, // only funds can have paid, not partner contributions
)
