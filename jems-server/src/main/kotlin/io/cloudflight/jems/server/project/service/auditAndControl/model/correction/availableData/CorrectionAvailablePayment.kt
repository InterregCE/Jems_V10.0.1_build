package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class CorrectionAvailablePayment(
    val fund: ProgrammeFund,

    val ecPayment: PaymentApplicationToEc?,
)
