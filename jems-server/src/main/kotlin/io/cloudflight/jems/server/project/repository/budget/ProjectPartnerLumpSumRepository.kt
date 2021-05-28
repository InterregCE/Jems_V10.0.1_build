package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumPerPartnerSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.UUID

@Repository
interface ProjectPartnerLumpSumRepository : CrudRepository<ProjectPartnerLumpSumEntity, UUID> {

    @Query("SELECT new io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumPerPartnerSumEntity(e.id.projectPartner, SUM(e.amount)) FROM #{#entityName} e WHERE e.id.projectPartner.id IN :ids GROUP BY e.id.projectPartner")
    fun sumLumpSumsPerPartner(@Param("ids") partnerIds: Set<Long>): List<ProjectLumpSumPerPartnerSumEntity>

    @Query("SELECT SUM(e.amount) FROM #{#entityName} e WHERE e.id.projectPartner.id = :partnerId")
    fun sumTotalForPartner(@Param("partnerId") partnerId: Long): BigDecimal?

    @Query(
        """
        SELECT SUM(entity.amount)
        FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS entity
        WHERE entity.project_partner_id = :partnerId
        """,
        nativeQuery = true
    )
    fun sumTotalForPartnerAsOfTimestamp(partnerId: Long, timestamp: Timestamp): BigDecimal?

}
