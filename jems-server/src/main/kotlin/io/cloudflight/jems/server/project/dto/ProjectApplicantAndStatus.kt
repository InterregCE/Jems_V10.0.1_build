package io.cloudflight.jems.server.project.dto

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus

data class ProjectApplicantAndStatus (
    val applicantId: Long,
    val projectStatus: ProjectApplicationStatus
)
