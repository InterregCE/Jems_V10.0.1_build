package io.cloudflight.jems.server.user.service.model.assignment

import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel

data class CollaboratorAssignedToProject(
    val userId: Long,
    val userEmail: String,
    val level: ProjectCollaboratorLevel,
)
