package io.cloudflight.ems.api.dto

import io.cloudflight.ems.api.dto.user.OutputUser
import java.time.ZonedDateTime

data class OutputProject (
    val id: Long?,
    val acronym: String,
    val applicant: OutputUser,
    val submissionDate: ZonedDateTime?,
    val projectStatus: OutputProjectStatus
)
