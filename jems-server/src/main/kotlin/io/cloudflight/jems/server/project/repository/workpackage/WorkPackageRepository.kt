package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import java.sql.Timestamp
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    fun findFirstByProjectIdAndId(projectId: Long, workPackageId: Long): WorkPackageEntity

    fun countAllByProjectId(projectId: Long): Long

    @Query(
        value ="""
             SELECT
             entity.id AS id, 
             entity.number as number,
             workPackageTransl.name as name
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageTransl ON entity.id = workPackageTransl.work_package_id
             WHERE entity.id = :workpackageId
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
             workPackageTransl.name as name,
             workPackageTransl.specific_objective as specificObjective,
             workPackageTransl.objective_and_audience as objectiveAndAudience
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageTransl ON entity.id = workPackageTransl.work_package_id
             WHERE entity.id = :workpackageId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findByIdAsOfTimestamp(
        workpackageId: Long,
        timestamp: Timestamp
    ): List<WorkPackageRow>
}
