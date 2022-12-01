package io.cloudflight.jems.api.project.dto.assignment

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.user.dto.UserSummaryDTO

data class ProjectUserDTO(
    val id: String?,
    val customIdentifier: String,
    val acronym: String,
    val projectStatus: ApplicationStatusDTO,
    val relatedCall: String,
    val users: Set<UserSummaryDTO>,
)
