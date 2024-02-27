package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal

data class CorrectionAvailableReportTmp(
    val partnerId: Long,

    val id: Long,
    val reportNumber: Int,

    val projectReportId: Long?,
    val projectReportNumber: Int?,

    val availableFund: ProgrammeFund,
    val fundShareTotal: BigDecimal,

    val ecPaymentId: Long?,
    val ecPaymentStatus: PaymentEcStatus?,
    val ecPaymentAccountingYear: AccountingYear?,
)
