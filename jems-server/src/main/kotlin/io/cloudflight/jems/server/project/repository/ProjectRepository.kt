package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<ProjectEntity, Long> {

    @EntityGraph(attributePaths = ["call", "currentStatus", "priorityPolicy.programmePriority"])
    override fun findAll(pageable: Pageable): Page<ProjectEntity>

    @EntityGraph(attributePaths = ["call", "currentStatus", "priorityPolicy.programmePriority"])
    fun findAllByApplicantId(applicantId: Long, pageable: Pageable): Page<ProjectEntity>

    @EntityGraph(attributePaths = ["call", "currentStatus", "priorityPolicy.programmePriority"])
    fun findAllByCurrentStatusStatusNot(status: ApplicationStatus, pageable: Pageable): Page<ProjectEntity>

}
