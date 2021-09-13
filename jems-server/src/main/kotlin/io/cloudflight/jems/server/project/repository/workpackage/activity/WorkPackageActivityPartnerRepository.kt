package io.cloudflight.jems.server.project.repository.workpackage.activity

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface WorkPackageActivityPartnerRepository : JpaRepository<WorkPackageActivityPartnerEntity, WorkPackageActivityPartnerId> {

    fun findAllByIdActivityIdIn(activityIds: Collection<Long>): MutableList<WorkPackageActivityPartnerEntity>

    fun deleteAllByIdActivityIdIn(activityIds: Collection<Long>)

    @Query(
        value = """
            SELECT
                entity.activity_id as activityId,
                entity.project_partner_id as projectPartnerId
                FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                WHERE   entity.activity_id = :activityId
             """,
        nativeQuery = true
    )
    fun findAllByActivityIdAsOfTimestamp(
        activityId: Long,
        timestamp: Timestamp
    ): List<WorkPackageActivityPartnerRow>

    @Query(
        value = """
             SELECT
             activity.id AS activityId,
             activity.work_package_id AS workPackageId,
             entity.project_partner_id as projectPartnerId
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN project_work_package_activity FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS activity ON entity.activity_id = activity.id
             WHERE activity.work_package_id IN :workPackageIds
             """,
        nativeQuery = true
    )
    fun findAllByWorkPackageIdsAsOfTimestamp(
        workPackageIds: Collection<Long>,
        timestamp: Timestamp
    ): List<WorkPackageActivityPartnerRow>
}
