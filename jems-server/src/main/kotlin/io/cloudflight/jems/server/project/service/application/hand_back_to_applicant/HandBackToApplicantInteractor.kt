package io.cloudflight.jems.server.project.service.application.hand_back_to_applicant

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface HandBackToApplicantInteractor {
    fun handBackToApplicant(projectId: Long): ApplicationStatus
}
