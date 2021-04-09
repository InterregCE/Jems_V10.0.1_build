package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectApi
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.InputProject
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.api.project.dto.cofinancing.ProjectPartnerBudgetCoFinancingDTO
import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudgetInteractor
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing.GetProjectBudgetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.toProjectPartnerBudgetDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectController(
    private val projectService: ProjectService,
    private val getProjectBudgetInteractor: GetProjectBudgetInteractor,
    private val getProjectBudgetCoFinancingInteractor: GetProjectBudgetCoFinancingInteractor,
    private val getProjectInteractor: GetProjectInteractor,
) : ProjectApi {

    /**
     * Here the @PreAuthorize annotation is missing because list is filtered based on restrictions inside the service
     */
    override fun getProjects(pageable: Pageable): Page<OutputProjectSimple> {
        return projectService.findAll(pageable)
    }

    @CanReadProject
    override fun getProjectById(projectId: Long): ProjectDetailDTO {
        return projectService.getById(projectId)
    }

    override fun getProjectCallSettingsById(projectId: Long): ProjectCallSettingsDTO =
        getProjectInteractor.getProjectCallSettings(projectId).toDto()

    @PreAuthorize("@projectAuthorization.canCreateProjectForCall(#project.projectCallId)")
    override fun createProject(project: InputProject): ProjectDetailDTO {
        return projectService.createProject(project)
    }

    @CanUpdateProject
    override fun updateProjectData(projectId: Long, project: InputProjectData): ProjectDetailDTO {
        return projectService.update(projectId, project)
    }

    override fun getProjectBudget(projectId: Long): List<ProjectPartnerBudgetDTO> =
        getProjectBudgetInteractor.getBudget(projectId = projectId).toProjectPartnerBudgetDTO()

    override fun getProjectCoFinancing(projectId: Long): List<ProjectPartnerBudgetCoFinancingDTO> =
        getProjectBudgetCoFinancingInteractor.getBudgetCoFinancing(projectId = projectId).toProjectPartnerBudgetDTO()
}
