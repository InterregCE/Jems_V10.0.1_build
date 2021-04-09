package io.cloudflight.jems.server.project.service.application.approve_application_with_conditions

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface ApproveApplicationWithConditionsInteractor {
    fun approveWithConditions(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus
}
