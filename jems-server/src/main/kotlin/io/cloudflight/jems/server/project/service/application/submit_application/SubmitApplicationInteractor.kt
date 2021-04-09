package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface SubmitApplicationInteractor {
    fun submit(projectId: Long): ApplicationStatus
}
