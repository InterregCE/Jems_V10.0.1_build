package io.cloudflight.jems.server.project.service.application.revert_application_to_contracted

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface RevertApplicationToContractedInteractor {
    fun revertApplicationToContracted(projectId: Long): ApplicationStatus
}
