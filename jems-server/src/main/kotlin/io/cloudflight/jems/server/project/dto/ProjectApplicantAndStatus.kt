package io.cloudflight.jems.server.project.dto

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO

data class ProjectApplicantAndStatus (
    val applicantId: Long,
    val projectStatus: ApplicationStatusDTO
)
