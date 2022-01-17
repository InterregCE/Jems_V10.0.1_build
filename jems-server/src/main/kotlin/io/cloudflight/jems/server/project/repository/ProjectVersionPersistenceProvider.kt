package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionId
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectVersionPersistenceProvider(
    private val projectVersionRepository: ProjectVersionRepository,
    private val userRepository: UserRepository,
) : ProjectVersionPersistence {

    @Transactional
    override fun createNewVersion(projectId: Long, status: ApplicationStatus, version: String, userId: Long) : ProjectVersion {
        projectVersionRepository.endCurrentVersion(projectId)
        return projectVersionRepository.save(
            ProjectVersionEntity(
                id = ProjectVersionId(version, projectId),
                user = userRepository.getById(userId)
            )
        ).toProjectVersion(status, true)
    }

    @Transactional(readOnly = true)
    override fun getLatestVersionOrNull(projectId: Long): String? =
        projectVersionRepository.findLatestVersion(projectId)

    @Transactional(readOnly = true)
    override fun getAllVersionsByProjectId(projectId: Long): List<ProjectVersion> =
        projectVersionRepository.findAllVersionsByProjectId(projectId).toProjectVersion()

}
