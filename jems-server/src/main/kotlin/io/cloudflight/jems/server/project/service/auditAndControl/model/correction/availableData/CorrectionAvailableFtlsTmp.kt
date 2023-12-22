package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

class CorrectionAvailableFtlsTmp(
    val partnerId: Long,

    val programmeLumpSumId: Long,
    val orderNr: Int,
    val name: Set<InputTranslation>,

    val availableFund: ProgrammeFund,

    val ecPaymentId: Long?,
    val ecPaymentStatus: PaymentEcStatus?,
    val ecPaymentAccountingYear: AccountingYear?,
)
