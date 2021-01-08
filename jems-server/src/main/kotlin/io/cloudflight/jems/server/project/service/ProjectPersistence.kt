package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings

interface ProjectPersistence {

    fun getProject(projectId: Long): Project

    fun getProjectCallSettingsForProject(projectId: Long): ProjectCallSettings

}
