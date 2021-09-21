package io.cloudflight.jems.server.project.service.result.update_project_results

import io.cloudflight.jems.server.project.service.result.model.ProjectResult

interface UpdateProjectResultsInteractor {
    fun updateResultsForProject(projectId: Long, projectResults: List<ProjectResult>): List<ProjectResult>
}
