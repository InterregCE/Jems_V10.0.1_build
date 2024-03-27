package io.cloudflight.jems.server.payments.model.account.finance.correction

import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import java.math.BigDecimal

data class PaymentAccountCorrectionExtension(
    val correctionId: Long,
    val paymentAccountId: Long?,
    val paymentAccountStatus: PaymentAccountStatus?,
    val auditControlStatus :AuditControlStatus,
    val comment: String?,

    val fundAmount: BigDecimal,

    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,

    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,

    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
)
