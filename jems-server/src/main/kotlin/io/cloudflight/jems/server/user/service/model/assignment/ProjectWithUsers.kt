package io.cloudflight.jems.server.user.service.model.assignment

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

data class ProjectWithUsers(
    val id: Long,
    val customIdentifier: String,
    val acronym: String,
    val projectStatus: ApplicationStatus,
    val assignedUserIds: Set<Long>,
)
