package io.cloudflight.ems.project.repository

import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : PagingAndSortingRepository<Project, Long> {

    fun findAllByApplicantId(applicantId: Long, pageable: Pageable): Page<Project>

    fun findAllByProjectStatusStatusNot(status: ProjectApplicationStatus, pageable: Pageable): Page<Project>

    fun findOneById(id: Long): Project?

}
