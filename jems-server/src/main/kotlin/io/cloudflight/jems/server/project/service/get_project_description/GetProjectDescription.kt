package io.cloudflight.jems.server.project.service.get_project_description

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import org.springframework.stereotype.Service

@Service
class GetProjectDescription(
    private val projectDescriptionPersistence: ProjectDescriptionPersistence
) : GetProjectDescriptionInteractor {

    @CanRetrieveProjectForm
    override fun getProjectDescription(projectId: Long, version: String?): ProjectDescription =
        projectDescriptionPersistence.getProjectDescription(projectId, version)

}
