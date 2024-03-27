package io.cloudflight.jems.server.payments.model.account

enum class PaymentAccountStatus {
    DRAFT,
    FINISHED;

    fun isFinished() = this == FINISHED
}
