package io.cloudflight.jems.server.project.entity.file

import io.cloudflight.jems.api.project.dto.file.ProjectFileType

data class FileMetadata (
    val name: String,
    val projectId: Long,
    val size: Long,
    val type: ProjectFileType
)
