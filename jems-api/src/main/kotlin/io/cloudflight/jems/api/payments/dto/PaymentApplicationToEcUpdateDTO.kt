package io.cloudflight.jems.api.payments.dto

data class PaymentApplicationToEcUpdateDTO(
    val id: Long?,
    val programmeFundId: Long,
    val accountingYearId: Long
)
