package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import java.time.ZonedDateTime

data class ProjectVersionDTO(
    val version: String,
    val createdAt: ZonedDateTime,
    val status: ApplicationStatusDTO,
)
