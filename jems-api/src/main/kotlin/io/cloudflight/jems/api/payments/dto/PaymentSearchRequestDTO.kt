package io.cloudflight.jems.api.payments.dto

import java.time.LocalDate

data class PaymentSearchRequestDTO (
    val paymentId: Long? = null,
    val paymentType: PaymentTypeDTO? = null,
    val projectIdentifiers: Set<String> = emptySet(),
    val projectAcronym: String? = null,

    val claimSubmissionDateFrom: LocalDate? = null,
    val claimSubmissionDateTo: LocalDate? = null,

    val approvalDateFrom: LocalDate? = null,
    val approvalDateTo: LocalDate? = null,

    val fundIds: Set<Long> = emptySet(),
    val lastPaymentDateFrom: LocalDate? = null,
    val lastPaymentDateTo: LocalDate? = null,
)
