package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion

interface ProjectVersionPersistence {

    fun createNewVersion(projectId: Long, version: String, status: ApplicationStatus, userId: Long): ProjectVersion

    fun getLatestVersionOrNull(projectId: Long): ProjectVersion?

    fun getAllVersionsByProjectId(projectId: Long): List<ProjectVersion>

    fun updateProjectLastVersion(projectId: Long) : ProjectVersion
}
