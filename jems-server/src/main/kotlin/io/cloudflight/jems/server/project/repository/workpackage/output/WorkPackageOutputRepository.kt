package io.cloudflight.jems.server.project.repository.workpackage.output

import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputRow
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface WorkPackageOutputRepository: PagingAndSortingRepository<WorkPackageOutputEntity, Long> {

    @EntityGraph(value = "WorkPackageOutputEntity.full")
    fun findAllByOutputIdWorkPackageIdIn(workPackageIds: Collection<Long>): Iterable<WorkPackageOutputEntity>

    fun findAllByOutputIdWorkPackageId(workPackageId: Long): Iterable<WorkPackageOutputEntity>

    @Query(
        value ="""
             SELECT
             entity.work_package_id AS workPackageId,
             entity.output_number AS outputNumber,
             entity.indicator_output_id as programmeOutputIndicatorId,
             (SELECT identifier
             FROM programme_indicator_output
             WHERE programme_indicator_output.id = entity.indicator_output_id) as programmeOutputIndicatorIdentifier,
             entity.target_value as targetValue,
             CONVERT(entity.period_number, INT) as periodNumber,
             workPackageOutputTransl.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageOutputTransl ON entity.work_package_id = workPackageOutputTransl.work_package_id AND entity.output_number = workPackageOutputTransl.output_number
             WHERE entity.work_package_id IN :workPackageIds
             ORDER BY entity.output_number
             """,
        nativeQuery = true
    )
    fun findAllByOutputIdWorkPackageIdAsOfTimestamp(
        workPackageIds: Collection<Long>,
        timestamp: Timestamp,
    ): List<WorkPackageOutputRow>
}
