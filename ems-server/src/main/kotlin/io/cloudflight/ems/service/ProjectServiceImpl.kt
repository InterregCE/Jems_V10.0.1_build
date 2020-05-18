package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.service.ProjectDtoUtilClass.Companion.getDtoFrom
import io.cloudflight.ems.service.ProjectDtoUtilClass.Companion.toEntity
import io.cloudflight.ems.repository.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepository
) : ProjectService {

    @Transactional(readOnly = true)
    override fun getProjects(page: Pageable): Page<OutputProject> {
        return projectRepo.findAll(page).map { getDtoFrom(it) }
    }

    @Transactional
    override fun createProject(project: InputProject): OutputProject {
        val createdProject = projectRepo.save(toEntity(project))
        return getDtoFrom(createdProject)
    }

}
