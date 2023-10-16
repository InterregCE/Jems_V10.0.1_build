package io.cloudflight.jems.api.payments.dto

data class PaymentEcStatusUpdateDTO(
    val status: PaymentEcStatusDTO,
    val availableToReOpen: Boolean
)
