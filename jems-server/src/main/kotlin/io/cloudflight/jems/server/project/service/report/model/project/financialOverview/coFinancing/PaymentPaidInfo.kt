package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing

import java.math.BigDecimal

data class PaymentPaidInfo(
    val amount: BigDecimal,
    val paid: BigDecimal,
)
