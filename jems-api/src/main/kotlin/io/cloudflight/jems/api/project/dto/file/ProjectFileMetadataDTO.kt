package io.cloudflight.jems.api.project.dto.file

import io.cloudflight.jems.api.user.dto.OutputUser
import java.time.ZonedDateTime

data class ProjectFileMetadataDTO(
    val id: Long,
    val projectId: Long,
    val name: String,
    val size: Long,
    val sizeString: String,
    val uploadedAt: ZonedDateTime,
    val uploadedBy: OutputUser,
    val description: String?
)
