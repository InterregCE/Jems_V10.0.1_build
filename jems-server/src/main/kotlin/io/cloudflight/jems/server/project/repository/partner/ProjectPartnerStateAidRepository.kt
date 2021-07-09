package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.server.project.entity.partner.state_aid.PartnerStateAidRow
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectPartnerStateAidRepository : JpaRepository<ProjectPartnerStateAidEntity, Long> {

    @Query(
        """
            SELECT
              entity.partner_id AS partnerId,
              entity.answer1 AS answer1,
              stateAidTransl.justification1 AS justification1,
              entity.answer2 AS answer2,
              stateAidTransl.justification2 AS justification2,
              entity.answer3 AS answer3,
              stateAidTransl.justification3 AS justification3,
              entity.answer4 AS answer4,
              stateAidTransl.justification4 AS justification4,
              stateAidTransl.language AS language
            FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
              LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS stateAidTransl ON entity.partner_id = stateAidTransl.partner_id
              WHERE entity.partner_id = :partnerId
              ORDER BY entity.partner_id
             """,
        nativeQuery = true
    )
    fun findPartnerStateAidByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerStateAidRow>

}
