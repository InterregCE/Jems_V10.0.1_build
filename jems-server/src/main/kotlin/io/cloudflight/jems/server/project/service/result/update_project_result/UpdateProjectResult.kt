package io.cloudflight.jems.server.project.service.result.update_project_result

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import org.springframework.stereotype.Service

@Service
class UpdateProjectResult (
    private val projectResultPersistence: ProjectResultPersistence
) : UpdateProjectResultInteractor {

    @CanUpdateProject
    override fun updateProjectResults(projectId: Long, projectResults: Set<ProjectResultDTO>) =
        projectResultPersistence.updateProjectResults(projectId, projectResults)

}
