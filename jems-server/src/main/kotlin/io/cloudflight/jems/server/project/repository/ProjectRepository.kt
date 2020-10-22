package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : PagingAndSortingRepository<Project, Long> {

    @EntityGraph(attributePaths = ["call", "projectStatus", "priorityPolicy.programmePriority"])
    override fun findAll(pageable: Pageable): Page<Project>

    @EntityGraph(attributePaths = ["call", "projectStatus", "priorityPolicy.programmePriority"])
    fun findAllByApplicantId(applicantId: Long, pageable: Pageable): Page<Project>

    @EntityGraph(attributePaths = ["call", "projectStatus", "priorityPolicy.programmePriority"])
    fun findAllByProjectStatusStatusNot(status: ProjectApplicationStatus, pageable: Pageable): Page<Project>

}
