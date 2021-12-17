package io.cloudflight.jems.server.project.service.application.approve_modification

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface ApproveModificationInteractor {
    fun approveModification(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus
}
