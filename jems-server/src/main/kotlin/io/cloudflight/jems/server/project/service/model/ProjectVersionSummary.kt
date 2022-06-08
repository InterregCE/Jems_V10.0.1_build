package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime

data class ProjectVersionSummary(
    val version: String,
    val projectId: Long,
    val createdAt: ZonedDateTime,
    val user: UserEntity,
)
