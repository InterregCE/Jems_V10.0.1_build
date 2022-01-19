package io.cloudflight.jems.server.project.service.save_project_version

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary
import io.cloudflight.jems.server.project.service.projectVersionSnapshotCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateNewProjectVersion(
    private val projectPersistence: ProjectPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher
) : CreateNewProjectVersionInteractor {

    @Transactional
    override fun create(projectId: Long): ProjectVersionSummary =
        projectVersionPersistence.getLatestVersionOrNull(projectId).let { lastVersionOrNull ->
            projectVersionPersistence.createNewVersion(
                projectId = projectId,
                version = ProjectVersionUtils.increaseMajor(lastVersionOrNull),
                userId = securityService.getUserIdOrThrow()
            ).also {
                auditPublisher.publishEvent(
                    projectVersionSnapshotCreated(context = this, projectPersistence.getProjectSummary(projectId), projectVersion = it)
                )
            }
        }
}
