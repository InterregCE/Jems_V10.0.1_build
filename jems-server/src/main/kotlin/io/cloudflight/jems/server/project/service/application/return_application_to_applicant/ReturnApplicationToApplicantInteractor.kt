package io.cloudflight.jems.server.project.service.application.return_application_to_applicant

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface ReturnApplicationToApplicantInteractor {
    fun returnToApplicant(projectId: Long): ApplicationStatus
}
