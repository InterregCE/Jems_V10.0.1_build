package io.cloudflight.jems.server.payments.model.regular

enum class PaymentEcStatus {
    Draft,
    Finished;

    fun isFinished() = this == Finished

}
