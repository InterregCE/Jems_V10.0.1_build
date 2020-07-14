package io.cloudflight.ems.api.dto

import java.time.ZonedDateTime

data class OutputProjectSimple (
    val id: Long?,
    val acronym: String,
    val submissionDate: ZonedDateTime?,
    val projectStatus: OutputProjectStatus
)
