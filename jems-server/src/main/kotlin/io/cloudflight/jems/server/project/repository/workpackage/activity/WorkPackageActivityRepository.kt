package io.cloudflight.jems.server.project.repository.workpackage.activity

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageDeliverableRow
import java.sql.Timestamp
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageActivityRepository : PagingAndSortingRepository<WorkPackageActivityEntity, Long> {

    @EntityGraph(value = "WorkPackageActivityEntity.full")
    fun findAllByWorkPackageIdIn(workPackageIds: Collection<Long>): Iterable<WorkPackageActivityEntity>

    fun findAllByWorkPackageId(workPackageId: Long): Iterable<WorkPackageActivityEntity>

    @Query(
        """
            SELECT
                entity.work_package_id as workPackageId,
                entity.activity_number as activityNumber,
                CONVERT(entity.start_period, INT) as startPeriod,
                CONVERT(entity.end_period, INT) as endPeriod,
                translation.*
                FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation
                    ON entity.work_package_id = translation.work_package_id
                    AND entity.activity_number = translation.activity_number
                WHERE entity.work_package_id = :workPackageId
             """,
        nativeQuery = true
    )
    fun findAllActivitiesByWorkPackageIdAsOfTimestamp(workPackageId: Long, timestamp: Timestamp): List<WorkPackageActivityRow>

    @Query(
        """
            SELECT
                entity.deliverable_number as deliverableNumber,
                CONVERT(entity.start_period, INT) as startPeriod,
                translation.*
                FROM project_work_package_activity_deliverable FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                LEFT JOIN project_work_package_activity_deliverable_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation
                    ON entity.activity_id = translation.source_entity_id
                WHERE entity.activity_id = :activityId
        """,
        nativeQuery = true
    )
    fun findAllDeliverablesByActivityIdAsOfTimestamp(activityId: Long, timestamp: Timestamp): List<WorkPackageDeliverableRow>

    @Query(
        """
            SELECT
                entity.work_package_id as workPackageId,
                entity.activity_number as activityNumber,
                CONVERT(entity.start_period, INT) as startPeriod,
                CONVERT(entity.end_period, INT) as endPeriod,
                translation.*
                FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation
                    ON entity.work_package_id = translation.work_package_id
                    AND entity.activity_number = translation.activity_number
                WHERE entity.work_package_id IN :workPackageIds
             """,
        nativeQuery = true
    )
    fun findAllByActivityIdWorkPackageIdAsOfTimestamp(workPackageIds: Collection<Long>, timestamp: Timestamp): List<WorkPackageActivityRow>

//    fun findAllByActivityId
}
