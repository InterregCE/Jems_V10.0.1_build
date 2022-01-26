package io.cloudflight.jems.server.project.controller.projectuser

import io.cloudflight.jems.api.project.dto.assignment.ProjectCollaboratorLevelDTO
import io.cloudflight.jems.api.project.dto.assignment.ProjectUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserCollaboratorDTO
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject

fun CollaboratorAssignedToProject.toDto() = ProjectUserCollaboratorDTO(
    userId = userId,
    userEmail = userEmail,
    level = ProjectCollaboratorLevelDTO.valueOf(level.name),
)

fun List<CollaboratorAssignedToProject>.toDto() = map { it.toDto() }

fun Set<UpdateProjectUserCollaboratorDTO>.toModel() = mapTo(HashSet()) {
    Pair(it.userEmail, ProjectCollaboratorLevel.valueOf(it.level.name))
}
