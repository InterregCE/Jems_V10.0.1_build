package io.cloudflight.jems.server.project.service.save_project_version

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateNewProjectVersion(
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val securityService: SecurityService,
) : CreateNewProjectVersionInteractor {

    @Transactional
    override fun create(projectId: Long): ProjectVersionSummary =
        projectVersionPersistence.getLatestVersionOrNull(projectId).let { lastVersionOrNull ->
            projectVersionPersistence.createNewVersion(
                projectId = projectId,
                version = ProjectVersionUtils.increaseMajor(lastVersionOrNull),
                userId = securityService.getUserIdOrThrow()
            )
        }
}
