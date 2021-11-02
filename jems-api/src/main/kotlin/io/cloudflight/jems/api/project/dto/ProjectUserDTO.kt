package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO

data class ProjectUserDTO(
    val id: Long?,
    val customIdentifier: String,
    val acronym: String,
    val projectStatus: ApplicationStatusDTO,
    val assignedUserIds: Set<Long>,
)
