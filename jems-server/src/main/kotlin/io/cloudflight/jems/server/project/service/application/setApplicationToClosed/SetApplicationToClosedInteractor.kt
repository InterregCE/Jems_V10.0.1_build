package io.cloudflight.jems.server.project.service.application.setApplicationToClosed

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface SetApplicationToClosedInteractor {
    fun setApplicationToClosed(projectId: Long): ApplicationStatus
}
