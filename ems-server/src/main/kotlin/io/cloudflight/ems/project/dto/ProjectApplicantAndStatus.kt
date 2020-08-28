package io.cloudflight.ems.project.dto

import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus

data class ProjectApplicantAndStatus (
    val applicantId: Long,
    val projectStatus: ProjectApplicationStatus
)
