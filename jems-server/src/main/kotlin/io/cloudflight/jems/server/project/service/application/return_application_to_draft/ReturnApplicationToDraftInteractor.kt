package io.cloudflight.jems.server.project.service.application.return_application_to_draft

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface ReturnApplicationToDraftInteractor {
    fun returnToDraft(projectId: Long): ApplicationStatus
}
