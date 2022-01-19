package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary

interface ProjectVersionPersistence {

    fun createNewVersion(projectId: Long, version: String, userId: Long): ProjectVersionSummary

    fun getLatestVersionOrNull(projectId: Long): String?

    fun getAllVersionsByProjectId(projectId: Long): List<ProjectVersion>
}
