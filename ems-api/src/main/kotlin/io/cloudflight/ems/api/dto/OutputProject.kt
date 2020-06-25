package io.cloudflight.ems.api.dto

import java.time.LocalDate

data class OutputProject (
    val id: Long?,
    val acronym: String,
    val applicant: OutputUser,
    val submissionDate: LocalDate
)
