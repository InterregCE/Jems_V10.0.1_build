package io.cloudflight.jems.server.project.service.result.get_project_result

import io.cloudflight.jems.server.project.service.result.model.ProjectResult

interface GetProjectResultInteractor {
    fun getResultsForProject(projectId: Long): List<ProjectResult>
}
