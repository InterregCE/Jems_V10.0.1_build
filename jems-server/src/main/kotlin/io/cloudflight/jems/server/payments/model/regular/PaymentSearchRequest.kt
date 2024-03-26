package io.cloudflight.jems.server.payments.model.regular

import java.time.LocalDate

data class PaymentSearchRequest(
    val paymentId: Long?,
    val paymentType: PaymentType?,
    val projectIdentifiers: Set<String>,
    val projectAcronym: String?,

    val claimSubmissionDateFrom: LocalDate?,
    val claimSubmissionDateTo: LocalDate?,

    val approvalDateFrom: LocalDate?,
    val approvalDateTo: LocalDate?,

    val fundIds: Set<Long>,
    val lastPaymentDateFrom: LocalDate?,
    val lastPaymentDateTo: LocalDate?,

    val ecPaymentIds: Set<Long?>,
    val contractingScoBasis: PaymentSearchRequestScoBasis?,
    val finalScoBasis: PaymentSearchRequestScoBasis?,
)
