package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class CorrectionAvailableFund(
    val fund: ProgrammeFund,
    val ecPayment: CorrectionEcPayment?,
    val disabled: Boolean,
)
