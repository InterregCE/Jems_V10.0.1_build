package io.cloudflight.jems.server.payments.repository.regular

import io.cloudflight.jems.server.payments.entity.PaymentContributionMetaEntity
import io.cloudflight.jems.server.payments.entity.PaymentLumpSumEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta

fun Collection<ContributionMeta>.toEntities() = map {
    PaymentContributionMetaEntity(
        projectId = it.projectId,
        partnerId = it.partnerId,
        lumpSum = PaymentLumpSumEntity(programmeLumpSumId = it.programmeLumpSumId, orderNr = it.orderNr),
        partnerContribution = it.partnerContribution,
        publicContribution = it.publicContribution,
        automaticPublicContribution = it.automaticPublicContribution,
        privateContribution = it.privateContribution,
    )
}


fun List<PaymentToEcExtensionEntity>.toModelList() = map {
    PaymentToEcExtension(
        paymentId = it.paymentId,
        ecPaymentId = it.paymentApplicationToEc?.id,
        ecPaymentStatus = it.paymentApplicationToEc?.status
    )
}
