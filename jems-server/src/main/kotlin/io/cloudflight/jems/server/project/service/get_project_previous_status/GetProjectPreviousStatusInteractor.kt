package io.cloudflight.jems.server.project.service.get_project_previous_status

import io.cloudflight.jems.server.project.service.model.ProjectStatus

interface GetProjectPreviousStatusInteractor {
    fun getProjectPreviousStatus(projectId: Long): ProjectStatus
}
