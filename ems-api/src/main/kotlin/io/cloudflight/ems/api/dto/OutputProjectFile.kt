package io.cloudflight.ems.api.dto

import java.time.ZonedDateTime

data class OutputProjectFile (
    val id: Long?,
    val identifier: String?,
    val description: String?,
    val size: Long?,
    val updated: ZonedDateTime?
)
