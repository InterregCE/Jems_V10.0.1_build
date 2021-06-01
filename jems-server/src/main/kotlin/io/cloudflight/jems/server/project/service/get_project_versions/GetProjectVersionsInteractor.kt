package io.cloudflight.jems.server.project.service.get_project_versions

import io.cloudflight.jems.server.project.service.model.ProjectVersion

interface GetProjectVersionsInteractor {
    fun getProjectVersions(projectId: Long): List<ProjectVersion>
}
