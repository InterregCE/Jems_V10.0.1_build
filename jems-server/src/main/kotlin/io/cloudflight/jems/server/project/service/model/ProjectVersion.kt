package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime

data class ProjectVersion(
    val version: String,
    val projectId: Long,
    val createdAt: ZonedDateTime,
    val user: UserEntity,
    val status: ApplicationStatus,
)
