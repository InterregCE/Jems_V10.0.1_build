package io.cloudflight.jems.server.project.service.save_project_version

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion

interface SaveProjectVersionInteractor {
    fun createNewVersion(projectId: Long, status: ApplicationStatus): ProjectVersion

    fun updateLastVersion(projectId: Long) : ProjectVersion
}
