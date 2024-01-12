package io.cloudflight.jems.server.payments.model.account

import java.math.BigDecimal
import java.time.LocalDate

data class PaymentAccountUpdate(
    val nationalReference: String,
    val technicalAssistance: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String,
    val comment: String
)
