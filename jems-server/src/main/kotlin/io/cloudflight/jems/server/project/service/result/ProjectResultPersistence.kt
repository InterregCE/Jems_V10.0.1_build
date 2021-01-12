package io.cloudflight.jems.server.project.service.result

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO

interface ProjectResultPersistence {

    fun updateProjectResults(projectId: Long, projectResults: Set<ProjectResultDTO> ): Set<ProjectResultDTO>

    fun getProjectResultsForProject(projectId: Long): Set<ProjectResultDTO>

}
