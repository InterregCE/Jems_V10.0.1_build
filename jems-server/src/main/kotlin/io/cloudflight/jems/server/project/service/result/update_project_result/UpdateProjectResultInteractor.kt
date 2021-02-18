package io.cloudflight.jems.server.project.service.result.update_project_result

import io.cloudflight.jems.server.project.service.result.model.ProjectResult

interface UpdateProjectResultInteractor {
    fun updateResultsForProject(projectId: Long, projectResults: List<ProjectResult>): List<ProjectResult>
}
