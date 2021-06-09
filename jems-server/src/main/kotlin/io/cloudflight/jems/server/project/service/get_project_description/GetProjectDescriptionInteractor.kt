package io.cloudflight.jems.server.project.service.get_project_description

import io.cloudflight.jems.server.project.service.model.ProjectDescription

interface GetProjectDescriptionInteractor {

    fun getProjectDescription(projectId: Long, version: String?): ProjectDescription

}
