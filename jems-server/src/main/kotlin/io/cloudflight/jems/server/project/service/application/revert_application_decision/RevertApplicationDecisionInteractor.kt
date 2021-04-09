package io.cloudflight.jems.server.project.service.application.revert_application_decision

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface RevertApplicationDecisionInteractor {
    fun revert(projectId: Long): ApplicationStatus
}
