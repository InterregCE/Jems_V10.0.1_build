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

    fun deleteAllByIdWorkPackageId(workPackageId: Long)

    @Query(
        value = """
            SELECT
                entity.work_package_id as workPackageId,
                entity.activity_number as activityNumber,
                entity.project_partner_id as projectPartnerId
                FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                WHERE   entity.work_package_id = :workPackageId
                    AND entity.activity_number = :activityNumber
             """,
        nativeQuery = true
    )
    fun findAllByWorkPackageIdAndActivityNumberAsOfTimestamp(
        workPackageId: Long,
        activityNumber: Int,
        timestamp: Timestamp
    ): List<WorkPackageActivityPartnerRow>

    @Query(
        value = """
             SELECT
             entity.work_package_id AS workPackageId,
             entity.activity_number AS activityNumber,
             entity.project_partner_id as projectPartnerId
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.work_package_id IN :workPackageIds
             """,
        nativeQuery = true
    )
    fun findAllByWorkPackageIdsAsOfTimestamp(
        workPackageIds: Collection<Long>,
        timestamp: Timestamp
    ): List<WorkPackageActivityPartnerRow>
}
