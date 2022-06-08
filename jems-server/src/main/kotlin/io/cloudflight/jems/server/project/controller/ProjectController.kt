package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectApi
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.ProjectCreateDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailFormDTO
import io.cloudflight.jems.api.project.dto.ProjectSearchRequestDTO
import io.cloudflight.jems.api.project.dto.ProjectVersionDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.api.project.dto.cofinancing.ProjectCoFinancingOverviewDTO
import io.cloudflight.jems.api.project.dto.cofinancing.ProjectPartnerBudgetCoFinancingDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivitySummaryDTO
import io.cloudflight.jems.server.project.controller.workpackage.toInvestmentSummaryDTOs
import io.cloudflight.jems.server.project.controller.workpackage.toSummariesDto
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudgetInteractor
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing.GetProjectBudgetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview.GetProjectCoFinancingOverviewInteractor
import io.cloudflight.jems.server.project.service.create_project.CreateProjectInteractor
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.project.service.get_project_versions.GetProjectVersionsInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.toDto
import io.cloudflight.jems.server.project.service.partner.cofinancing.toProjectPartnerBudgetDTO
import io.cloudflight.jems.server.project.service.workpackage.activity.get_activity.GetActivityInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_project_investment_summaries.GetProjectInvestmentSummariesInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectController(
    private val projectService: ProjectService,
    private val getProjectBudgetInteractor: GetProjectBudgetInteractor,
    private val getProjectBudgetCoFinancingInteractor: GetProjectBudgetCoFinancingInteractor,
    private val getProjectCoFinancingOverviewInteractor: GetProjectCoFinancingOverviewInteractor,
    private val getProjectInteractor: GetProjectInteractor,
    private val createProjectInteractor: CreateProjectInteractor,
    private val getProjectVersionsInteractor: GetProjectVersionsInteractor,
    private val getProjectInvestmentSummaries: GetProjectInvestmentSummariesInteractor,
    private val getProjectActivitiesInteractor: GetActivityInteractor
) : ProjectApi {

    override fun getAllProjects(
        page: Int?, size: Int?, sortProperty: String, sortDirection: String, searchRequest: ProjectSearchRequestDTO?
    ): Page<OutputProjectSimple> {
        val pageRequest = if (page == null || size == null)
            Pageable.unpaged()
        else PageRequest.of(page, size, Direction.valueOf(sortDirection.uppercase()), sortProperty)

        return getProjectInteractor.getAllProjects(pageRequest, searchRequest?.toModel()).toDto()
    }

    override fun getMyProjects(pageable: Pageable): Page<OutputProjectSimple> =
        getProjectInteractor.getMyProjects(pageable).toDto()

    override fun getProjectById(projectId: Long, version: String?): ProjectDetailDTO =
        getProjectInteractor.getProjectDetail(projectId, version).toDto()

    override fun getProjectFormById(projectId: Long, version: String?): ProjectDetailFormDTO =
        getProjectInteractor.getProjectForm(projectId, version).toDto()

    override fun getProjectCallSettingsById(projectId: Long): ProjectCallSettingsDTO =
        getProjectInteractor.getProjectCallSettings(projectId).toDto()

    override fun createProject(project: ProjectCreateDTO): ProjectDetailDTO =
        createProjectInteractor.createProject(project.acronym, project.projectCallId).toDto()

    override fun updateProjectForm(projectId: Long, project: InputProjectData): ProjectDetailFormDTO =
        projectService.update(projectId, project).toDto()

    override fun getProjectBudget(projectId: Long, version: String?): List<ProjectPartnerBudgetDTO> =
        getProjectBudgetInteractor.getBudget(projectId = projectId, version).toDTO()

    override fun getProjectCoFinancing(projectId: Long, version: String?): List<ProjectPartnerBudgetCoFinancingDTO> =
        getProjectBudgetCoFinancingInteractor.getBudgetCoFinancing(projectId = projectId, version)
            .toProjectPartnerBudgetDTO()

    override fun getProjectCoFinancingOverview(projectId: Long, version: String?): ProjectCoFinancingOverviewDTO =
        getProjectCoFinancingOverviewInteractor.getProjectCoFinancingOverview(projectId, version).toDto()

    override fun getProjectVersions(projectId: Long): Collection<ProjectVersionDTO> =
        getProjectVersionsInteractor.getProjectVersions(projectId).toDTOs()

    override fun getProjectInvestmentSummaries(projectId: Long, version: String?) =
        getProjectInvestmentSummaries.getProjectInvestmentSummaries(projectId, version).toInvestmentSummaryDTOs()

    override fun getProjectActivities(projectId: Long, version: String?): List<WorkPackageActivitySummaryDTO> =
        getProjectActivitiesInteractor.getActivitiesForProject(projectId, version).toSummariesDto()
}
