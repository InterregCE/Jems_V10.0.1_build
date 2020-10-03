package io.cloudflight.jems.server.dto

import io.cloudflight.jems.api.dto.ProjectFileType

data class FileMetadata (
    val name: String,
    val projectId: Long,
    val size: Long,
    val type: ProjectFileType
)
