package io.cloudflight.jems.api.project.dto

data class ProjectPeriodDTO(
    val projectId: Long,
    val number: Int,
    val start: Int,
    val end: Int
)
