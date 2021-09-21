package io.cloudflight.jems.server.project.repository.partner.budget.options

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptionsEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.Optional

@Repository
interface ProjectPartnerBudgetOptionsRepository : CrudRepository<ProjectPartnerBudgetOptionsEntity, Long> {

    @Query(
        """
            SELECT entity.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS entity
             WHERE  entity.partner_id = :partnerId
             ORDER BY entity.partner_id
             """,
        nativeQuery = true
    )
    fun findByPartnerIdAsOfTimestamp(partnerId: Long, timestamp: Timestamp): Optional<ProjectPartnerBudgetOptionsEntity>

    @Query(
        """
            SELECT entity.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS entity
             WHERE entity.partner_id IN :partnerIds
             ORDER BY entity.partner_id
             """,
        nativeQuery = true
    )
    fun findAllByPartnerIdsAsOfTimestamp(partnerIds: Set<Long>, timestamp: Timestamp): List<ProjectPartnerBudgetOptionsEntity>

}
