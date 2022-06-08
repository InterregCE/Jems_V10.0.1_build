package io.cloudflight.jems.server.project.repository.budget.cofinancing

import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerContributionRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionSpfEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectPartnerContributionSpfRepository : JpaRepository<ProjectPartnerContributionSpfEntity, Long> {

    fun findAllByPartnerId(partnerId: Long): MutableList<ProjectPartnerContributionSpfEntity>

    fun deleteByPartnerId(partnerId: Long)

    @Query(
        """
            SELECT
             contribution.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS contribution
             WHERE contribution.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerContributionSpfByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerContributionRow>

}
