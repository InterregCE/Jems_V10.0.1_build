package io.cloudflight.jems.server.project.controller.result

import io.cloudflight.jems.api.project.dto.result.ProjectResultUpdateRequestDTO
import io.cloudflight.jems.api.project.result.ProjectResultApi
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.project.service.result.get_project_result.GetProjectResultInteractor
import io.cloudflight.jems.server.project.service.result.update_project_results.UpdateProjectResultsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectResultController(
    private val getProjectResultInteractor: GetProjectResultInteractor,
    private val updateProjectResultsInteractor: UpdateProjectResultsInteractor
) : ProjectResultApi {
    override fun getProjectResults(projectId: Long, version: String?): List<ProjectResultDTO> {
        return getProjectResultInteractor.getResultsForProject(projectId, version).toDto()
    }

    override fun updateProjectResults(projectId: Long, projectResultUpdateRequests: List<ProjectResultUpdateRequestDTO>): List<ProjectResultDTO> {
        return updateProjectResultsInteractor.updateResultsForProject(projectId, projectResultUpdateRequests.toModel()).toDto()
    }
}
