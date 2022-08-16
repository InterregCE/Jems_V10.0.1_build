package io.cloudflight.jems.server.project.service.contracting.model

import java.time.LocalDate

data class ProjectPeriodForMonitoring(
    val number: Int,
    val start: Int,
    val end: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
)
