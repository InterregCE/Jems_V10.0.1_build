package io.cloudflight.jems.api.project.dto.assignment

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO

data class ProjectUserDTO(
    val id: String?,
    val customIdentifier: String,
    val acronym: String,
    val projectStatus: ApplicationStatusDTO,
    val relatedCall: String,
    val users: Set<Long>,
)