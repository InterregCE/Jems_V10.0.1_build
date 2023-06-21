package io.cloudflight.jems.server.payments.repository.regular

import io.cloudflight.jems.server.payments.entity.PaymentContributionMetaEntity
import io.cloudflight.jems.server.payments.entity.PaymentLumpSumEntity
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
