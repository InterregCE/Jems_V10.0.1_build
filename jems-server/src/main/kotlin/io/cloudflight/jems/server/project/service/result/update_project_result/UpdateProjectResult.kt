package io.cloudflight.jems.server.project.service.result.update_project_result

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import org.springframework.stereotype.Service

@Service
class UpdateProjectResult (
    private val projectResultPersistence: ProjectResultPersistence
) : UpdateProjectResultInteractor {

    companion object {
        private const val MAX_RESULTS_PER_PROJECT = 20L
    }

    @CanUpdateProject
    override fun updateProjectResults(projectId: Long, projectResults: Set<ProjectResultDTO>): Set<ProjectResultDTO> {
        if (projectResults.size > MAX_RESULTS_PER_PROJECT)
            throw I18nValidationException(i18nKey = "project.results.max.allowed.reached")

        return projectResultPersistence.updateProjectResults(projectId, projectResults)
    }

}
