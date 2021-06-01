package io.cloudflight.jems.server.project.service.create_new_project_version

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion
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
    override fun create(projectId: Long, status: ApplicationStatus): ProjectVersion =
        projectVersionPersistence.getLatestVersionOrNull(projectId).let { latestProjectVersion ->
            projectVersionPersistence.createNewVersion(
                projectId = projectId, status = status,
                version = ProjectVersionUtils.increaseMajor(latestProjectVersion?.version),
                userId = securityService.getUserIdOrThrow()
            ).also {
                auditPublisher.publishEvent(
                    projectVersionSnapshotCreated(context = this, projectPersistence.getProjectSummary(projectId), projectVersion = it)
                )
            }
        }
}
