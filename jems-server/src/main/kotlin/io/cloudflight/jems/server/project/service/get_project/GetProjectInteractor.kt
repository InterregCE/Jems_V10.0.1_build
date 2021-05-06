package io.cloudflight.jems.server.project.service.get_project

import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings

interface GetProjectInteractor {

    fun getProjectCallSettings(projectId: Long): ProjectCallSettings

    fun getProject(projectId: Long, version: Int?): Project

}
