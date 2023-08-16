package io.cloudflight.jems.server.payments.model.ec

data class PaymentApplicationToEcUpdate(
    val id: Long?,
    val programmeFundId: Long,
    val accountingYearId: Long
)

