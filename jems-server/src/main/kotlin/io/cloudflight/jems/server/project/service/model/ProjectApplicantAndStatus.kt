package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

data class ProjectApplicantAndStatus (
    val applicantId: Long,
    val projectStatus: ApplicationStatus
)
