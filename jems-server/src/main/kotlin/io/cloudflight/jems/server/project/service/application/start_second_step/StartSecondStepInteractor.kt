package io.cloudflight.jems.server.project.service.application.start_second_step

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface StartSecondStepInteractor {
    fun startSecondStep(projectId: Long): ApplicationStatus
}
