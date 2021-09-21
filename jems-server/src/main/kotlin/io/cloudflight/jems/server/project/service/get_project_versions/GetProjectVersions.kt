package io.cloudflight.jems.server.project.service.get_project_versions

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectVersion
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectVersions(
    private val projectVersionPersistence: ProjectVersionPersistence
) : GetProjectVersionsInteractor {

    @CanRetrieveProjectVersion
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectVersionsExceptions::class)
    override fun getProjectVersions(projectId: Long): List<ProjectVersion> =
        projectVersionPersistence.getAllVersionsByProjectId(projectId)
}
