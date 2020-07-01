package io.cloudflight.ems.controller

import io.cloudflight.ems.api.ProjectApi
import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.service.ProjectService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectController(
    private val projectService: ProjectService
) : ProjectApi {

    override fun getProjects(pageable: Pageable): Page<OutputProject> {
        return projectService.findAll(pageable)
    }

    override fun createProject(project: InputProject): OutputProject {
        return projectService.createProject(project)
    }

    @PreAuthorize("@projectAuthorization.canAccessProject(#id)")
    override fun getProjectById(id: Long): OutputProject {
        return projectService.getById(id)
    }
}
