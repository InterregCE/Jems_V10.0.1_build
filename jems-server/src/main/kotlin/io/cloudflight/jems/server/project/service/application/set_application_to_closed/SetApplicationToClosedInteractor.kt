package io.cloudflight.jems.server.project.service.application.set_application_to_closed

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface SetApplicationToClosedInteractor {
    fun setApplicationToClosed(projectId: Long): ApplicationStatus
}
