package io.cloudflight.jems.server.project.service.get_project

import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.Project
import org.springframework.stereotype.Service

@Service
class GetProject(private val persistence: ProjectPersistence) : GetProjectInteractor {

    @CanReadProject
    override fun getProjectCallSettings(projectId: Long): ProjectCallSettings =
        persistence.getProjectCallSettings(projectId)

    @CanReadProject
    override fun getProject(projectId: Long, version: Int?): Project =
        persistence.getProject(projectId, version)

}
