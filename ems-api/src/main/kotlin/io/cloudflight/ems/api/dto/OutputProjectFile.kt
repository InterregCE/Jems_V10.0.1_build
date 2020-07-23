package io.cloudflight.ems.api.dto

import io.cloudflight.ems.api.dto.user.OutputUser
import java.time.ZonedDateTime

data class OutputProjectFile (
    val id: Long?,
    val name: String,
    val author: OutputUser,
    val type: ProjectFileType,
    val description: String?,
    val size: Long,
    val updated: ZonedDateTime
)
