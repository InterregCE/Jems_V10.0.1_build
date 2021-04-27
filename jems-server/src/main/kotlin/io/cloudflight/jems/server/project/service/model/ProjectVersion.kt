package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import java.sql.Timestamp

data class ProjectVersion(
    val version: Int,
    val projectId: Long,
    val createdAt: Timestamp,
    val user: UserEntity,
    val status: ApplicationStatus,
)
