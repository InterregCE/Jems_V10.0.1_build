package io.cloudflight.jems.server.project.service.application.refuse_application

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface RefuseApplicationInteractor {
    fun refuse(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus
}
