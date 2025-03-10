package io.cloudflight.jems.api.payments.dto

data class PaymentApplicationToEcDetailDTO (
    val id: Long,
    val status: PaymentEcStatusDTO,
    val availableToReOpen: Boolean,
    val paymentApplicationToEcSummary: PaymentApplicationToEcSummaryDTO
)
