package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionId
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectVersionPersistenceProvider(
    private val projectVersionRepository: ProjectVersionRepository,
    private val optimizationProjectVersionRepository: OptimizationProjectVersionRepository,
    private val userRepository: UserRepository,
) : ProjectVersionPersistence {

    @Transactional
    override fun createNewVersion(projectId: Long, version: String, userId: Long): ProjectVersionSummary {
        projectVersionRepository.endCurrentVersion(projectId)
        return projectVersionRepository.save(
            ProjectVersionEntity(
                id = ProjectVersionId(version, projectId),
                user = userRepository.getById(userId)
            )
        ).toProjectVersionSummary()
    }

    @Transactional(readOnly = true)
    override fun getLatestVersionOrNull(projectId: Long): String? =
        projectVersionRepository.findLatestVersion(projectId)

    @Transactional(readOnly = true)
    override fun getAllVersionsByProjectId(projectId: Long): List<ProjectVersion> =
        projectVersionRepository.findAllVersionsByProjectId(projectId).toProjectVersion()

    @Transactional(readOnly = true)
    override fun getLatestApprovedOrCurrent(projectId: Long): String {
        val versionsSorted = getAllVersionsByProjectId(projectId)
        return versionsSorted.firstOrNull { it.status.isApproved() }?.version
            ?: versionsSorted.first().version
    }

    @Transactional(readOnly = true)
    override fun getAllVersions(): List<ProjectVersion> =
        projectVersionRepository.findAllVersions().toProjectVersion()

    @Transactional
    override fun saveTimestampForApprovedApplication(projectId: Long) =
        optimizationProjectVersionRepository.saveOptimizationProjectVersion(projectId)

    @Transactional
    override fun updateTimestampForApprovedModification(projectId: Long) =
        optimizationProjectVersionRepository.updateOptimizationProjectVersion(projectId)
}
