package io.cloudflight.jems.server.project.service.file.model

import io.cloudflight.jems.server.user.service.model.UserSummary
import java.time.ZonedDateTime

data class ProjectFileMetadata(
    val id: Long,
    val projectId: Long,
    val name: String,
    val size: Long,
    val uploadedAt: ZonedDateTime,
    val uploadedBy: UserSummary,
    val description: String?
)
