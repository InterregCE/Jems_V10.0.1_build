package io.cloudflight.jems.server.payments.model.ec

enum class PaymentToEcOverviewType {
    DoesNotFallUnderArticle94Nor95,
    FallsUnderArticle94Or95,
    Correction;

    fun isCorrectionOrArt94or95() = (this == Correction || this == FallsUnderArticle94Or95)
}
