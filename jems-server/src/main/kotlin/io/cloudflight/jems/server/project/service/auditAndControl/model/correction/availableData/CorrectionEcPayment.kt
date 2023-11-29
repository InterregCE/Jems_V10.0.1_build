package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus

data class CorrectionEcPayment(
    val id: Long,
    val status: PaymentEcStatus,
    val accountingYear: AccountingYear,
)
