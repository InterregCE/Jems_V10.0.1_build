package io.cloudflight.jems.server.user.service.model.assignment

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.service.model.UserSummary

data class ProjectWithUsers(
    val id: String,
    val customIdentifier: String,
    val acronym: String,
    val projectStatus: ApplicationStatus,
    val relatedCall: String,
    val users: Set<UserSummary>,
)
