package io.cloudflight.ems.dto

import io.cloudflight.ems.api.dto.ProjectFileType

data class FileMetadata (
    val name: String,
    val projectId: Long,
    val size: Long,
    val type: ProjectFileType
)
