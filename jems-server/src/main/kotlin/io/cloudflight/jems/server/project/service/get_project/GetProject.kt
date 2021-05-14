package io.cloudflight.jems.server.project.service.get_project

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjects
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GetProject(
    private val persistence: ProjectPersistence,
    private val securityService: SecurityService,
) : GetProjectInteractor {

    @CanRetrieveProject
    override fun getProjectCallSettings(projectId: Long): ProjectCallSettings =
        persistence.getProjectCallSettings(projectId)

    @CanRetrieveProject
    override fun getProject(projectId: Long, version: Int?): Project =
        persistence.getProject(projectId, version)

    @CanRetrieveProjects
    override fun getAllProjects(pageable: Pageable): Page<ProjectSummary> =
        persistence.getProjects(pageable)

    override fun getMyProjects(pageable: Pageable): Page<ProjectSummary> =
        persistence.getProjects(
            pageable = pageable,
            filterByOwnerId = securityService.currentUser!!.user.id,
        )

}
