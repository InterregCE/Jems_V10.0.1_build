package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultRow
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectResultRepository : CrudRepository<ProjectResultEntity, Long> {
    @Query(
        value ="""
             SELECT
             entity.baseline,
             entity.result_number AS resultNumber,
             entity.indicator_result_id as programmeResultIndicatorId,
             programmeResultIndicatorIdentifier.identifier as programmeResultIndicatorIdentifier,
             programmeResultIndicatorIdentifierTransl.language as programmeResultIndicatorLanguage,
             programmeResultIndicatorIdentifierTransl.name as programmeResultIndicatorName,
             programmeResultIndicatorIdentifierTransl.measurement_unit as programmeResultIndicatorMeasurementUnit,
             entity.target_value as targetValue,
             CONVERT(entity.period_number, INT) as periodNumber,
             projectResultTransl.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS projectResultTransl ON entity.project_id = projectResultTransl.project_id AND entity.result_number = projectResultTransl.result_number
             LEFT JOIN programme_indicator_result AS programmeResultIndicatorIdentifier ON entity.indicator_result_id = programmeResultIndicatorIdentifier.id
             LEFT JOIN programme_indicator_result_transl AS programmeResultIndicatorIdentifierTransl ON programmeResultIndicatorIdentifier.id = programmeResultIndicatorIdentifierTransl.source_entity_id
             WHERE entity.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun getProjectResultsByProjectId(
        projectId: Long,
        timestamp: Timestamp,
    ): List<ProjectResultRow>
}
