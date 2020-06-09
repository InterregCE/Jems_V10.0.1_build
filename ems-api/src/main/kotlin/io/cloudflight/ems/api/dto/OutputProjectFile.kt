package io.cloudflight.ems.api.dto

import java.time.ZonedDateTime

data class OutputProjectFile (
    val id: Long?,
    val name: String?,
    val description: String?,
    val size: Long?,
    val updated: ZonedDateTime?,
    val creator: String?
)
