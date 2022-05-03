package io.cloudflight.jems.server.project.repository.workpackage.activity

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageDeliverableRow
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp


@Repository
interface WorkPackageActivityRepository : JpaRepository<WorkPackageActivityEntity, Long> {

    @EntityGraph(value = "WorkPackageActivityEntity.full")
    fun findAllByWorkPackageIdIn(workPackageIds: Collection<Long>): Iterable<WorkPackageActivityEntity>

    fun findAllByWorkPackageId(workPackageId: Long): Iterable<WorkPackageActivityEntity>

    @Query(
        """
            SELECT
                entity.id,
                entity.work_package_id as workPackageId,
                workpackage.number as workPackageNumber,
                entity.activity_number as activityNumber,
                CONVERT(entity.start_period, INT) as startPeriod,
                CONVERT(entity.end_period, INT) as endPeriod,
                translation.*
                FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation
                    ON entity.id = translation.source_entity_id
                LEFT JOIN project_work_package FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workpackage
                    ON entity.work_package_id = workpackage.id
                WHERE entity.work_package_id = :workPackageId
             """,
        nativeQuery = true
    )
    fun findAllActivitiesByWorkPackageIdAsOfTimestamp(workPackageId: Long, timestamp: Timestamp): List<WorkPackageActivityRow>

    @Query(
        """
            SELECT
                entity.id,
                entity.deliverable_number as deliverableNumber,
                CONVERT(entity.start_period, INT) as startPeriod,
                translation.*
                FROM #{#entityName}_deliverable FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                LEFT JOIN #{#entityName}_deliverable_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation
                    ON entity.id = translation.source_entity_id
                WHERE entity.activity_id = :activityId
        """,
        nativeQuery = true
    )
    fun findAllDeliverablesByActivityIdAsOfTimestamp(activityId: Long, timestamp: Timestamp): List<WorkPackageDeliverableRow>

    @Query(
        """
            SELECT
                entity.id,
                entity.work_package_id as workPackageId,
                entity.activity_number as activityNumber,
                CONVERT(entity.start_period, INT) as startPeriod,
                CONVERT(entity.end_period, INT) as endPeriod,
                translation.*
                FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation
                    ON entity.id = translation.source_entity_id
                WHERE entity.work_package_id IN :workPackageIds
             """,
        nativeQuery = true
    )
    fun findAllByActivityIdWorkPackageIdAsOfTimestamp(workPackageIds: Collection<Long>, timestamp: Timestamp): List<WorkPackageActivityRow>

    @Query(
        """
            SELECT
                entity.id,
                entity.work_package_id as workPackageId,
                workpackage.number as workPackageNumber,
                entity.activity_number as activityNumber,
                CONVERT(entity.start_period, INT) as startPeriod,
                CONVERT(entity.end_period, INT) as endPeriod,
                translation.*
                FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation
                    ON entity.id = translation.source_entity_id
                LEFT JOIN project_work_package FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workpackage
                    ON entity.work_package_id = workpackage.id
                WHERE entity.id IN :activityIds
             """,
        nativeQuery = true
    )
    fun findAllByActivityIdInAsOfTimestamp(activityIds: Collection<Long>, timestamp: Timestamp): List<WorkPackageActivityRow>

}
