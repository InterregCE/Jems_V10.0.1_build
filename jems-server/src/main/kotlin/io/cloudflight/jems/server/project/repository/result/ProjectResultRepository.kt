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
             entity.result_number AS resultNumber,
             entity.indicator_result_id as programmeResultIndicatorId,
             (SELECT identifier
                 FROM programme_indicator_result
                 WHERE programme_indicator_result.id = entity.indicator_result_id) as programmeResultIndicatorIdentifier,
             entity.target_value as targetValue,
             CONVERT(entity.period_number, INT) as periodNumber,
             projectResultTransl.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS projectResultTransl ON entity.project_id = projectResultTransl.project_id AND entity.result_number = projectResultTransl.result_number
             WHERE entity.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun getProjectResultsByProjectId(
        projectId: Long,
        timestamp: Timestamp,
    ): List<ProjectResultRow>
}
