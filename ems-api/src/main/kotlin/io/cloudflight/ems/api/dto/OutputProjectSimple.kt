package io.cloudflight.ems.api.dto

import java.time.ZonedDateTime

data class OutputProjectSimple (
    val id: Long?,
    val acronym: String,
    val projectStatus: OutputProjectStatus,
    val firstSubmissionDate: ZonedDateTime?,
    val lastResubmissionDate: ZonedDateTime?
)
