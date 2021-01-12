package io.cloudflight.jems.server.project.service.get_project

import io.cloudflight.jems.server.project.service.model.ProjectCallSettings

interface GetProjectInteractor {
    fun getProjectCallSettings(projectId: Long): ProjectCallSettings
}
