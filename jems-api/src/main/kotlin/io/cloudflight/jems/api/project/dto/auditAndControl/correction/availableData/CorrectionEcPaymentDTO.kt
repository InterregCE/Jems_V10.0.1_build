package io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData

import io.cloudflight.jems.api.payments.dto.PaymentEcStatusDTO
import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO

data class CorrectionEcPaymentDTO(
    val id: Long,
    val status: PaymentEcStatusDTO,
    val accountingYear: AccountingYearDTO,
)
