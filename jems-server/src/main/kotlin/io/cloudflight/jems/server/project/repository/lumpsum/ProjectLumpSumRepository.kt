package io.cloudflight.jems.server.project.repository.lumpsum

import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumRow
import java.sql.Timestamp
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectLumpSumRepository : PagingAndSortingRepository<ProjectLumpSumEntity, Long> {

    @Query(
        """
            SELECT
             entity.project_id as projectId,
             entity.order_nr as orderNr,
             entity.end_period as endPeriod,
             entity.programme_lump_sum_id as programmeLumpSumId,
             partnerLumpSum.project_partner_id as projectPartnerId,
             partnerLumpSum.amount as amount
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN project_partner_lump_sum FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS partnerLumpSum ON partnerLumpSum.project_id = entity.project_id AND partnerLumpSum.order_nr = entity.order_nr 
             WHERE entity.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectLumpSumRow>
}