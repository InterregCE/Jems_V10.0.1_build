package io.cloudflight.jems.server.project.service.application.revertApplicationToContracted

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface RevertApplicationToContractedInteractor {
    fun revertApplicationToContracted(projectId: Long): ApplicationStatus
}
