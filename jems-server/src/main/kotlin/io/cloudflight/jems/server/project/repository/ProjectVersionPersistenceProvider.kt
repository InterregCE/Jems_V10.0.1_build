package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionId
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.user.repository.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectVersionPersistenceProvider(
    private val projectVersionRepository: ProjectVersionRepository,
    private val userRepository: UserRepository,
) : ProjectVersionPersistence {

    @Transactional
    override fun createNewVersion(projectId: Long, version: Int, status: ApplicationStatus, userId: Long) =
        projectVersionRepository.save(
            ProjectVersionEntity(
                id = ProjectVersionId(version, projectId),
                user = userRepository.getOne(userId),
                status = status
            )
        ).toProjectVersion()

    @Transactional(readOnly = true)
    override fun getLatestVersionOrNull(projectId: Long): ProjectVersion? =
        projectVersionRepository.findFirstByIdProjectIdOrderByCreatedAtDesc(projectId)?.toProjectVersion()

}
