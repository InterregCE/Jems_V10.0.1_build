package io.cloudflight.jems.server.project.service.application.reject_modification

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface RejectModificationInteractor {
    fun reject(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus
}
