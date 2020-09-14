package io.cloudflight.ems.project.controller

import io.cloudflight.ems.api.project.ProjectApi
import io.cloudflight.ems.api.project.dto.InputProject
import io.cloudflight.ems.api.project.dto.InputProjectData
import io.cloudflight.ems.api.project.dto.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.InputProjectManagement
import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.project.dto.OutputProjectSimple
import io.cloudflight.ems.project.service.ProjectService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectController(
    private val projectService: ProjectService
) : ProjectApi {

    /**
     * Here the @PreAuthorize annotation is missing because list is filtered based on restrictions inside the service
     */
    override fun getProjects(pageable: Pageable): Page<OutputProjectSimple> {
        return projectService.findAll(pageable)
    }

    @PreAuthorize("@projectAuthorization.canCreateProjectForCall(#project.projectCallId)")
    override fun createProject(project: InputProject): OutputProject {
        return projectService.createProject(project)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#id)")
    override fun getProjectById(id: Long): OutputProject {
        return projectService.getById(id)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#id)")
    override fun updateProjectData(id: Long, project: InputProjectData): OutputProject {
        return projectService.update(id, project)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#id)")
    override fun updateProjectManagement(id: Long, project: InputProjectManagement): OutputProject {
        return projectService.updateProjectManagement(id, project)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#id)")
    override fun updateProjectLongTermPlans(id: Long, project: InputProjectLongTermPlans): OutputProject {
        return projectService.updateProjectLongTermPlans(id, project)
    }

}
