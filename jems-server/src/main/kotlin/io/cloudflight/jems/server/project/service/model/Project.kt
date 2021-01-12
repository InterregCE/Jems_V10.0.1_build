package io.cloudflight.jems.server.project.service.model

data class Project (
    val id: Long,
    val periods: Collection<ProjectPeriod>,
    // more to be added
)
