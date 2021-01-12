package io.cloudflight.jems.server.project.service.result.get_project_result

import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import org.springframework.stereotype.Service

@Service
class GetProjectResult (
    private val projectResultPersistence: ProjectResultPersistence
) : GetProjectResultInteractor {

    @CanReadProject
    override fun getProjectResultsForProject(projectId: Long) =
        projectResultPersistence.getProjectResultsForProject(projectId)

}
