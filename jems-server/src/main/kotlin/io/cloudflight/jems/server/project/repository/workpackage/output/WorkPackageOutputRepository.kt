package io.cloudflight.jems.server.project.repository.workpackage.output

import io.cloudflight.jems.server.project.entity.workpackage.output.OutputRowWithTranslations
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

    fun findAllByOutputIdWorkPackageIdOrderByOutputIdOutputNumber(workPackageId: Long): Iterable<WorkPackageOutputEntity>

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

    @Query(
        value ="""
            SELECT
                output.work_package_id as workPackageId,
                wp.number as workPackageNumber,
                output.output_number as number,
                outputTransl.title as title,
                output.target_value as targetValue,
                programmeOutput.id as programmeOutputId,
                programmeResult.id as programmeResultId,
                outputTransl.language as language
            FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS output
                LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS outputTransl
                    ON output.work_package_id = outputTransl.work_package_id AND output.output_number = outputTransl.output_number
                LEFT JOIN project_work_package FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp as wp
                    ON wp.id = output.work_package_id
                LEFT JOIN programme_indicator_output as programmeOutput
                    ON output.indicator_output_id = programmeOutput.id
                LEFT JOIN programme_indicator_result as programmeResult
                    ON programmeOutput.result_indicator_id = programmeResult.id
            WHERE wp.project_id = :projectId
            ORDER BY wp.number, output.output_number
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdAsOfTimestampOrderedByNumbers(projectId: Long, timestamp: Timestamp): List<OutputRowWithTranslations>

    @Query(
        value ="""
            SELECT
                output.work_package_id as workPackageId,
                wp.number as workPackageNumber,
                output.output_number as number,
                outputTransl.title as title,
                output.target_value as targetValue,
                programmeOutput.id as programmeOutputId,
                programmeResult.id as programmeResultId,
                outputTransl.language as language
            FROM #{#entityName} AS output
                LEFT JOIN #{#entityName}_transl AS outputTransl
                    ON output.work_package_id = outputTransl.work_package_id AND output.output_number = outputTransl.output_number
                LEFT JOIN project_work_package as wp
                    ON wp.id = output.work_package_id
                LEFT JOIN programme_indicator_output as programmeOutput
                    ON output.indicator_output_id = programmeOutput.id
                LEFT JOIN programme_indicator_result as programmeResult
                    ON programmeOutput.result_indicator_id = programmeResult.id
            WHERE wp.project_id = :projectId
            ORDER BY wp.number, output.output_number
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdOrderedByNumbers(projectId: Long): List<OutputRowWithTranslations>

}
