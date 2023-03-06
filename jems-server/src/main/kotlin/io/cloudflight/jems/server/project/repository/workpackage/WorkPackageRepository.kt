package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputRow
import java.sql.Timestamp
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageRepository: PagingAndSortingRepository<WorkPackageEntity, Long> {

    @EntityGraph(value = "WorkPackageEntity.withTranslatedValues")
    fun findAllByProjectId(projectId: Long): List<WorkPackageEntity>

    fun findAllByProjectId(projectId: Long, sort: Sort): Iterable<WorkPackageEntity>

    fun countAllByProjectId(projectId: Long): Long

    @Query(
        value ="""
             SELECT
             entity.id AS id,
             entity.number as number,
             workPackageTransl.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageTransl ON entity.id = workPackageTransl.source_entity_id
             WHERE entity.project_id = :projectId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdAsOfTimestamp(
        projectId: Long,
        timestamp: Timestamp,
    ): List<WorkPackageRow>

    @Query(
        value = """
             SELECT
             entity.id AS id,
             entity.number as number,
             workPackageTransl.*,
             workPackageTransl.specific_objective as specificObjective,
             workPackageTransl.objective_and_audience as objectiveAndAudience
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageTransl ON entity.id = workPackageTransl.source_entity_id
             WHERE entity.id = :workPackageId
             """,
        nativeQuery = true
    )
    fun findByIdAsOfTimestamp(
        workPackageId: Long,
        timestamp: Timestamp
    ): List<WorkPackageRow>

    @Query(
        value ="""
            SELECT
                entity.work_package_id AS workPackageId,
                entity.output_number AS outputNumber,
                entity.indicator_output_id as programmeOutputIndicatorId,
                programmeOutputIndicatorIdentifier.identifier as programmeOutputIndicatorIdentifier,
                programmeOutputIndicatorIdentifierTransl.language as programmeOutputIndicatorLanguage,
                programmeOutputIndicatorIdentifierTransl.name as programmeOutputIndicatorName,
                programmeOutputIndicatorIdentifierTransl.measurement_unit as programmeOutputIndicatorMeasurementUnit,
                entity.target_value as targetValue,
                CONVERT(entity.period_number, INT) as periodNumber,
                workPackageOutputTransl.*
             FROM #{#entityName}_output FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_output_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageOutputTransl ON entity.work_package_id = workPackageOutputTransl.work_package_id AND entity.output_number = workPackageOutputTransl.output_number
             LEFT JOIN programme_indicator_output AS programmeOutputIndicatorIdentifier ON entity.indicator_output_id = programmeOutputIndicatorIdentifier.id
             LEFT JOIN programme_indicator_output_transl AS programmeOutputIndicatorIdentifierTransl ON programmeOutputIndicatorIdentifier.id = programmeOutputIndicatorIdentifierTransl.source_entity_id
             WHERE entity.work_package_id = :workPackageId
             ORDER BY entity.output_number
             """,
        nativeQuery = true
    )
    fun findOutputsByWorkPackageIdAsOfTimestamp(
        workPackageId: Long,
        timestamp: Timestamp,
    ): List<WorkPackageOutputRow>

    @Query(
        value ="""
             SELECT
             entity.id,
             entity.number,

             workPackageTransl.name,
             workPackageTransl.specific_objective AS specificObjective,
             workPackageTransl.objective_and_audience AS objectiveAndAudience,
             workPackageTransl.language
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageTransl ON entity.id = workPackageTransl.source_entity_id
             WHERE entity.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun findWorkPackagesBaseByProjectIdAsOfTimestamp(
        projectId: Long,
        timestamp: Timestamp,
    ): List<WorkPackageRow>
}
