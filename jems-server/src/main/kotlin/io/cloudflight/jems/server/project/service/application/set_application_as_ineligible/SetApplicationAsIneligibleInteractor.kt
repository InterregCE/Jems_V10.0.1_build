package io.cloudflight.jems.server.project.service.application.set_application_as_ineligible

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface SetApplicationAsIneligibleInteractor {
    fun setAsIneligible(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus
}
