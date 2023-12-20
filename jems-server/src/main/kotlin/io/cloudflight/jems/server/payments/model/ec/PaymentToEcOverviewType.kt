package io.cloudflight.jems.server.payments.model.ec

enum class PaymentToEcOverviewType {
    DoesNotFallUnderArticle94Nor95,
    FallsUnderArticle94Or95,
    Correction;

    fun isCorrection() = this == Correction
}
