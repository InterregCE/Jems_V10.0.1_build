package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectRow
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectRepository : JpaRepository<ProjectEntity, Long> {

    @Query(
        """
            SELECT
             entity.*, entity.step2_active as step2Active,
             translation.*,
             period.number as periodNumber, period.start as periodStart, period.end as periodEnd
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.project_id
             LEFT JOIN #{#entityName}_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS period ON entity.id = period.project_id
             WHERE entity.id = :id
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findByIdAsOfTimestamp(
        id: Long, timestamp: Timestamp
    ): List<ProjectRow>

    @EntityGraph(attributePaths = ["call", "currentStatus", "priorityPolicy.programmePriority"])
    override fun findAll(pageable: Pageable): Page<ProjectEntity>

    @EntityGraph(attributePaths = ["call", "currentStatus", "priorityPolicy.programmePriority"])
    fun findAllByApplicantId(applicantId: Long, pageable: Pageable): Page<ProjectEntity>

    @EntityGraph(attributePaths = ["call", "currentStatus", "priorityPolicy.programmePriority"])
    fun findAllByCurrentStatusStatusNot(status: ApplicationStatus, pageable: Pageable): Page<ProjectEntity>

}
