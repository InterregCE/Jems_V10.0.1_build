package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion

interface ProjectVersionPersistence {

    fun createNewVersion(projectId: Long, status: ApplicationStatus, version: String, userId: Long): ProjectVersion

    fun getLatestVersionOrNull(projectId: Long): String?

    fun getAllVersionsByProjectId(projectId: Long): List<ProjectVersion>
}
