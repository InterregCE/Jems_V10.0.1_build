package io.cloudflight.jems.server.project.controller.result

import io.cloudflight.jems.api.project.dto.result.InputProjectResultDTO
import io.cloudflight.jems.api.project.result.ProjectResultApi
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.project.service.result.get_project_result.GetProjectResultInteractor
import io.cloudflight.jems.server.project.service.result.update_project_result.UpdateProjectResultInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectResultController(
    private val getProjectResultInteractor: GetProjectResultInteractor,
    private val updateProjectResultInteractor: UpdateProjectResultInteractor
) : ProjectResultApi {
    override fun getProjectResults(projectId: Long): List<ProjectResultDTO> {
        return getProjectResultInteractor.getResultsForProject(projectId).toDto()
    }

    override fun updateProjectResults(projectId: Long, projectResults: List<InputProjectResultDTO>): List<ProjectResultDTO> {
        return updateProjectResultInteractor.updateResultsForProject(projectId, projectResults.toModel()).toDto()
    }
}
