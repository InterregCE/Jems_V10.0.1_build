package io.cloudflight.jems.server.project.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.DateTimePath
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodRow
import io.cloudflight.jems.server.project.entity.ProjectRow
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.ZonedDateTime
import java.util.Optional


@Repository
interface ProjectRepository : JpaRepository<ProjectEntity, Long>, QuerydslPredicateExecutor<ProjectEntity> {

    companion object {
        private val project = QProjectEntity.projectEntity

        private fun likeIdentifier(id: String?) =
            if (id.isNullOrBlank()) null
            else project.id.like("%${id}%").or(project.customIdentifier.like("%${id}%"))

        private fun likeAcronym(acronym: String?) =
            if (acronym.isNullOrBlank()) null
            else project.acronym.likeIgnoreCase("%${acronym}%")

        private fun isAfter(date: ZonedDateTime?, datePath: DateTimePath<ZonedDateTime>) =
            if (date == null) null
            else datePath.after(date)

        private fun isBefore(date: ZonedDateTime?, datePath: DateTimePath<ZonedDateTime>) =
            if (date == null) null
            else datePath.before(date)

        private fun hasAnySpecificObjective(objectives: Set<ProgrammeObjectivePolicy>?) =
            if (objectives.isNullOrEmpty()) null else project.priorityPolicy.programmeObjectivePolicy.`in`(objectives)

        private fun hasAnyStatus(statuses: Set<ApplicationStatus>?) =
            if (statuses.isNullOrEmpty()) null else project.currentStatus.status.`in`(statuses)

        private fun hasAnyCallId(callIds: Set<Long>?) =
            if (callIds.isNullOrEmpty()) null else project.call.id.`in`(callIds)

        fun buildSearchPredicate(searchRequest: ProjectSearchRequest?): Predicate =
            ExpressionUtils.allOf(
                likeIdentifier(searchRequest?.id),
                likeAcronym(searchRequest?.acronym),
                isAfter(searchRequest?.firstSubmissionFrom, project.firstSubmission.updated),
                isBefore(searchRequest?.firstSubmissionTo, project.firstSubmission.updated),
                isAfter(searchRequest?.lastSubmissionFrom, project.lastResubmission.updated),
                isBefore(searchRequest?.lastSubmissionTo, project.lastResubmission.updated),
                hasAnySpecificObjective(searchRequest?.objectives),
                hasAnyStatus(searchRequest?.statuses),
                hasAnyCallId(searchRequest?.calls)
            ) ?: BooleanBuilder()
    }

    @Query(
        """
            SELECT
             entity.*,
             entity.custom_identifier as customIdentifier,
             translation.*,

             entity.programme_priority_policy_objective_policy as programmePriorityPolicyObjectivePolicy,
             programmePrioObj.code as programmePriorityPolicyCode,
             programmePrioObj.programme_priority_id as programmePriorityId,

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
             LEFT JOIN programme_priority_specific_objective AS programmePrioObj ON entity.programme_priority_policy_objective_policy = programmePrioObj.programme_objective_policy_code

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
    fun findAllByIdIn(projectIds: Collection<Long>, pageable: Pageable): Page<ProjectEntity>
}
