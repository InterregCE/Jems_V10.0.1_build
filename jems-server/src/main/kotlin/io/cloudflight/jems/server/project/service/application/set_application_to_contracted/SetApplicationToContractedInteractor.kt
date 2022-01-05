package io.cloudflight.jems.server.project.service.application.set_application_to_contracted

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface SetApplicationToContractedInteractor {
    fun setApplicationToContracted(projectId: Long): ApplicationStatus
}
