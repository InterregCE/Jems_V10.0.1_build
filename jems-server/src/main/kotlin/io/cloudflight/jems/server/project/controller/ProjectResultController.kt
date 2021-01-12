package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectResultApi
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.project.service.result.get_project_result.GetProjectResultInteractor
import io.cloudflight.jems.server.project.service.result.update_project_result.UpdateProjectResultInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectResultController(
    private val getProjectResultInteractor: GetProjectResultInteractor,
    private val updateProjectResultInteractor: UpdateProjectResultInteractor
) : ProjectResultApi {
    override fun getProjectResults(projectId: Long): Set<ProjectResultDTO> {
        return getProjectResultInteractor.getProjectResultsForProject(projectId)
    }

    override fun updateProjectResults(projectId: Long, projectResults: Set<ProjectResultDTO>): Set<ProjectResultDTO> {
        return updateProjectResultInteractor.updateProjectResults(projectId, projectResults)
    }
}
