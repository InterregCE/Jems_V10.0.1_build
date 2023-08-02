package io.cloudflight.jems.api.payments.dto

data class PaymentApplicationsToEcUpdateDTO(
    val id: Long?,
    val programmeFundId: Long,
    val accountingYearId: Long
)
