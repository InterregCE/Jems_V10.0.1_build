package io.cloudflight.jems.server.project.repository.lumpsum

import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumRow
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumRowForProgrammeLocking
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
             CONVERT(entity.end_period, INT) as endPeriod,
             entity.programme_lump_sum_id as programmeLumpSumId,
             partnerLumpSum.project_partner_id as projectPartnerId,
             partnerLumpSum.amount as amount,
             CONVERT(programmeLumpSum.is_fast_track, INT) as fastTrack,
             CONVERT(entity.is_ready_for_payment, INT) as readyForPayment,
             entity.comment as comment,
             entity.payment_enabled_date as paymentEnabledDate,
             entity.last_approved_version_before_ready_for_payment as lastApprovedVersionBeforeReadyForPayment
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN project_partner_lump_sum FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS partnerLumpSum ON partnerLumpSum.project_id = entity.project_id AND partnerLumpSum.order_nr = entity.order_nr
             LEFT JOIN programme_lump_sum AS programmeLumpSum ON programmeLumpSum.id = entity.programme_lump_sum_id
             WHERE entity.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectLumpSumRow>

    @Query(
        """
            SELECT new io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumRowForProgrammeLocking(
                entity.id.projectId,
                entity.id.orderNr,
                entity.endPeriod,
                entity.programmeLumpSum.id,
                entity.isReadyForPayment,
                entity.comment
            )
             FROM #{#entityName} AS entity
             WHERE entity.programmeLumpSum.id = :programmeLumpSumId
             """
    )
    fun findAllByProgrammeLumpSumId(programmeLumpSumId: Long): List<ProjectLumpSumRowForProgrammeLocking>

    fun getByIdProjectId(projectId: Long): List<ProjectLumpSumEntity>
}
