package io.cloudflight.ems.api.dto

import java.time.LocalDate

data class InputProject (
    val acronym: String?,
    val submissionDate: LocalDate?
)
