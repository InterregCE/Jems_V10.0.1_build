package io.cloudflight.jems.server.user.service.model.assignment

import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.UserStatus

data class CollaboratorAssignedToProject(
    val userId: Long,
    val userEmail: String,
    val sendNotificationsToEmail: Boolean,
    val userStatus: UserStatus,
    val level: ProjectCollaboratorLevel,
)
