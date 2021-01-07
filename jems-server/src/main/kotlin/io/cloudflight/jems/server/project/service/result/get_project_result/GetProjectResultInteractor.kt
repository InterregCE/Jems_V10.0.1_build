package io.cloudflight.jems.server.project.service.result.get_project_result

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO

interface GetProjectResultInteractor {
    fun getProjectResultsForProject(projectId: Long): Set<ProjectResultDTO>
}
