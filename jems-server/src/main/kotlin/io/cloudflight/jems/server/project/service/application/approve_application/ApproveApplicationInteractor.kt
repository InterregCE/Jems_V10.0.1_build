package io.cloudflight.jems.server.project.service.application.approve_application

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface ApproveApplicationInteractor {
    fun approve(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus
}
