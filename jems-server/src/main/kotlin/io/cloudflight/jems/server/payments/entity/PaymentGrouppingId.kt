package io.cloudflight.jems.server.payments.entity

data class PaymentGrouppingId (
    val orderNr: Int,
    val partnerId: Long,
    val programmeFundId: Long,
)
