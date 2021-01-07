package io.cloudflight.jems.server.project.service.result.update_project_result

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO

interface UpdateProjectResultInteractor {
    fun updateProjectResults(projectId: Long, projectResults: Set<ProjectResultDTO>): Set<ProjectResultDTO>
}
