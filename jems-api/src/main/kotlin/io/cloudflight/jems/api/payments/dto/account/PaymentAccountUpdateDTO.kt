package io.cloudflight.jems.api.payments.dto.account

import java.math.BigDecimal
import java.time.LocalDate

data class PaymentAccountUpdateDTO (
    val nationalReference: String,
    val technicalAssistance: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String,
    val comment: String
)
