package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.assignment.CollaboratorLevelDTO
import io.cloudflight.jems.api.project.dto.assignment.ProjectUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserCollaboratorDTO
import io.cloudflight.jems.server.user.entity.CollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject

fun CollaboratorAssignedToProject.toDto() = ProjectUserCollaboratorDTO(
    userId = userId,
    userEmail = userEmail,
    level = CollaboratorLevelDTO.valueOf(level.name),
)

fun List<CollaboratorAssignedToProject>.toDto() = map { it.toDto() }

fun Set<UpdateProjectUserCollaboratorDTO>.toModel() = mapTo(HashSet()) {
    Pair(it.userEmail, CollaboratorLevel.valueOf(it.level.name))
}
