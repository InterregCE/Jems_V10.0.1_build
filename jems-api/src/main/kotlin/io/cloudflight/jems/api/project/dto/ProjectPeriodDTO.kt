package io.cloudflight.jems.api.project.dto

import java.time.LocalDate

data class ProjectPeriodDTO(
    val number: Int,
    val start: Int,
    val end: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)
