package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.Project
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPersistenceProvider(
    private val projectRepository: ProjectRepository,
) : ProjectPersistence {

    @Transactional(readOnly = true)
    override fun getProject(projectId: Long): Project =
        getProjectOrThrow(projectId).toModel()

    @Transactional(readOnly = true)
    override fun getProjectCallSettingsForProject(projectId: Long): ProjectCallSettings =
        getProjectOrThrow(projectId).call.toSettingsModel()

    private fun getProjectOrThrow(projectId: Long) =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

}
