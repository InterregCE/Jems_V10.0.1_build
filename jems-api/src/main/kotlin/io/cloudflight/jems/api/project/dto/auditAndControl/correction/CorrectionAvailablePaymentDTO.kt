package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO

data class CorrectionAvailablePaymentDTO(
    val fund: ProgrammeFundDTO,

    val ecPayment: PaymentApplicationToEcDTO?
)
