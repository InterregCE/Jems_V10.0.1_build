package io.cloudflight.jems.server.payments.model.regular

import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentPartnerInstallment(
    val id: Long? = null,
    val fundId: Long,
    val lumpSumId: Long?,
    val orderNr: Int?,
    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate?,
    val comment: String? = null,

    val isSavePaymentInfo: Boolean? = null,
    val savePaymentInfoUser: OutputUser? = null,
    val savePaymentDate: LocalDate? = null,
    val isPaymentConfirmed: Boolean? = null,
    val paymentConfirmedUser: OutputUser? = null,
    val paymentConfirmedDate: LocalDate? = null,

    val correction: AuditControlCorrection?,
)
