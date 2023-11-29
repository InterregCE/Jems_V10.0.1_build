package io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO

data class CorrectionAvailableFundDTO(
    val fund: ProgrammeFundDTO,
    val ecPayment: CorrectionEcPaymentDTO?,
)
