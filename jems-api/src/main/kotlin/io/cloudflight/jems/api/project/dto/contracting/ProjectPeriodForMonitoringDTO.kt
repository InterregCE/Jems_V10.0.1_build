package io.cloudflight.jems.api.project.dto.contracting

import java.time.LocalDate

data class ProjectPeriodForMonitoringDTO(
    val number: Int,
    val start: Int,
    val end: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
)
