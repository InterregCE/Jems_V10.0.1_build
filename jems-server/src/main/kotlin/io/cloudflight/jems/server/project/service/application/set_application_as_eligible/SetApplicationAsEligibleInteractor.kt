package io.cloudflight.jems.server.project.service.application.set_application_as_eligible

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface SetApplicationAsEligibleInteractor {
    fun setAsEligible(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus
}
