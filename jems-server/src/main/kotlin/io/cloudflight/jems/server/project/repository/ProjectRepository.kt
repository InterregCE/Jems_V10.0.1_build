package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodRow
import io.cloudflight.jems.server.project.entity.ProjectRow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.Optional


@Repository
interface ProjectRepository : JpaRepository<ProjectEntity, Long> {

    @Query(
        """
            SELECT
             entity.*,
             entity.custom_identifier as customIdentifier,
             translation.*,

             ps.id as statusId,
             ps.status,
             ps.updated,
             ps.decision_date as decisionDate,
             ps.entry_into_force_date as entryIntoForceDate,
             ps.note,

             account.name,
             account.surname,
             account.email,
             account.user_status as userStatus,
             account.id as userId,

             accountRole.id as roleId,
             accountRole.name as roleName

             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.project_id
             LEFT JOIN project_status AS ps ON ps.id = entity.project_status_id
             LEFT JOIN account AS account ON account.id = ps.account_id
             LEFT JOIN account_role AS accountRole ON accountRole.id = account.account_role_id

             WHERE entity.id = :id
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findByIdAsOfTimestamp(
        id: Long, timestamp: Timestamp
    ): List<ProjectRow>


    @Query(
        """
            SELECT count(entity) > 0
            FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
            WHERE entity.id = :id
             """,
        nativeQuery = true
    )
    fun existsByIsAsOfTimestamp(id: Long): Boolean


    @Query(
        """
            SELECT
             period.number as periodNumber,
              period.start as periodStart,
               period.end as periodEnd
             FROM #{#entityName}_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS period
             WHERE period.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun findPeriodsByProjectIdAsOfTimestamp(
        projectId: Long, timestamp: Timestamp
    ): List<ProjectPeriodRow>

    @Query("SELECT e.call.id FROM #{#entityName} e where e.id=:projectId")
    fun findCallIdFor(projectId: Long): Optional<Long>

    @EntityGraph(attributePaths = ["call", "currentStatus", "priorityPolicy.programmePriority"])
    override fun findAll(pageable: Pageable): Page<ProjectEntity>

    @EntityGraph(attributePaths = ["call", "currentStatus", "priorityPolicy.programmePriority"])
    fun findAllByApplicantIdOrIdIn(applicantId: Long, projectIds: Collection<Long>, pageable: Pageable): Page<ProjectEntity>
}
