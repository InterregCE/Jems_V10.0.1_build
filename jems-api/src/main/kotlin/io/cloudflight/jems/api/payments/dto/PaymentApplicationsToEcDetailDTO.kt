package io.cloudflight.jems.api.payments.dto

data class PaymentApplicationsToEcDetailDTO (
    val id: Long,
    val status: PaymentEcStatusDTO,
    val paymentApplicationsToEcSummary: PaymentApplicationsToEcSummaryDTO
)
