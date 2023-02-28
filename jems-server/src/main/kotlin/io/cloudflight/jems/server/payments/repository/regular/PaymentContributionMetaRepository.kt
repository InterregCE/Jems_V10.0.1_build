package io.cloudflight.jems.server.payments.repository.regular

import io.cloudflight.jems.server.payments.entity.PaymentContributionMetaEntity
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.PartnerContributionSplit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentContributionMetaRepository: JpaRepository<PaymentContributionMetaEntity, Long> {

    fun findByProjectIdAndLumpSumOrderNrIn(projectId: Long, orderNrs: Set<Int>): List<PaymentContributionMetaEntity>

    @Query("""
        SELECT new io.cloudflight.jems.server.payments.model.regular.contributionMeta.PartnerContributionSplit(
            COALESCE(SUM(pcm.partnerContribution), 0),
            COALESCE(SUM(pcm.publicContribution), 0),
            COALESCE(SUM(pcm.automaticPublicContribution), 0),
            COALESCE(SUM(pcm.privateContribution), 0)
        )
        FROM #{#entityName} pcm
        WHERE pcm.partnerId = :partnerId
    """)
    fun getContributionCumulative(partnerId: Long): PartnerContributionSplit

}
