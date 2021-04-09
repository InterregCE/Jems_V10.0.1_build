package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

data class ProjectSummary(
    val id: Long,
    val acronym: String,
    val status: ApplicationStatus
)
