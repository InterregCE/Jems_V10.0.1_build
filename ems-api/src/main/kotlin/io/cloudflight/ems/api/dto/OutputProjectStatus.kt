package io.cloudflight.ems.api.dto

import io.cloudflight.ems.api.dto.user.OutputUser
import java.time.ZonedDateTime

data class OutputProjectStatus (
    val id: Long?,
    val status: ProjectApplicationStatus,
    val user: OutputUser,
    val updated: ZonedDateTime,
    val note: String? = null
)
