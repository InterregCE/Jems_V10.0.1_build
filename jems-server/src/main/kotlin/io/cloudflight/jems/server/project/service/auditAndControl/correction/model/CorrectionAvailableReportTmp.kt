package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class CorrectionAvailableReportTmp(
    val partnerId: Long,

    val id: Long,
    val reportNumber: Int,

    val projectReportId: Long?,
    val projectReportNumber: Int?,
    val availableReportFunds: List<ProgrammeFund>,

    val paymentFund: ProgrammeFund?,

    val ecPaymentId: Long?,
    val ecPaymentStatus: PaymentEcStatus?,
    val ecPaymentAccountingYear: AccountingYear?,
)
