package io.cloudflight.jems.api.project.dto.assignment

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO

data class ProjectWithUsersDTO(
    val id: String?,
    val acronym: String?,
    val statuses: Set<ApplicationStatusDTO>?,
    val calls: Set<Long>?,
    val users: Set<Long>?,
)