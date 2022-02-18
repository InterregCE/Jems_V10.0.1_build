package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.ProjectCreateDTO
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.ProjectDetailFormDTO
import io.cloudflight.jems.api.project.dto.ProjectVersionDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.api.project.dto.cofinancing.ProjectCoFinancingOverviewDTO
import io.cloudflight.jems.api.project.dto.cofinancing.ProjectPartnerBudgetCoFinancingDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivitySummaryDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.InvestmentSummaryDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Api("Project")
interface ProjectApi {

    companion object {
        private const val ENDPOINT_API_PROJECT = "/api/project"
    }

    @ApiOperation("Returns all project applications in the system")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_PROJECT)
    fun getAllProjects(pageable: Pageable): Page<OutputProjectSimple>

    @ApiOperation("Returns applications of current user")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_PROJECT/mine")
    fun getMyProjects(pageable: Pageable): Page<OutputProjectSimple>

    @ApiOperation("Returns a project application details by id")
    @GetMapping("$ENDPOINT_API_PROJECT/byId/{projectId}")
    fun getProjectById(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): ProjectDetailDTO

    @ApiOperation("Returns a project application form by id")
    @GetMapping("$ENDPOINT_API_PROJECT/byId/{projectId}/form")
    fun getProjectFormById(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): ProjectDetailFormDTO

    @ApiOperation("Returns call setting of a call related to this application")
    @GetMapping("$ENDPOINT_API_PROJECT/{projectId}/callSettings")
    fun getProjectCallSettingsById(@PathVariable projectId: Long): ProjectCallSettingsDTO

    @ApiOperation("Creates new project application")
    @PostMapping(ENDPOINT_API_PROJECT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProject(@RequestBody project: ProjectCreateDTO): ProjectDetailDTO

    @ApiOperation("Update project-related data")
    @PutMapping("$ENDPOINT_API_PROJECT/{projectId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectForm(@PathVariable projectId: Long, @RequestBody project: InputProjectData): ProjectDetailFormDTO

    @ApiOperation("Returns project budget for all partners")
    @GetMapping("$ENDPOINT_API_PROJECT/{projectId}/budget")
    fun getProjectBudget(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerBudgetDTO>

    @ApiOperation("Returns project co-financing for all partners")
    @GetMapping("$ENDPOINT_API_PROJECT/{projectId}/coFinancing")
    fun getProjectCoFinancing(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerBudgetCoFinancingDTO>

    @ApiOperation("Returns project co-financing for all partners")
    @GetMapping("$ENDPOINT_API_PROJECT/{projectId}/coFinancingOverview")
    fun getProjectCoFinancingOverview(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): ProjectCoFinancingOverviewDTO

    @ApiOperation("Returns project versions")
    @GetMapping("$ENDPOINT_API_PROJECT/{projectId}/versions")
    fun getProjectVersions(@PathVariable projectId: Long): Collection<ProjectVersionDTO>

    @GetMapping("$ENDPOINT_API_PROJECT/{projectId}/summaries")
    fun getProjectInvestmentSummaries(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<InvestmentSummaryDTO>

    @GetMapping("$ENDPOINT_API_PROJECT/{projectId}/activities")
    fun getProjectActivities(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<WorkPackageActivitySummaryDTO>
}
