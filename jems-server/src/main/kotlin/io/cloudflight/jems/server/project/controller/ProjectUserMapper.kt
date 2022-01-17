package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.assignment.ProjectUserDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserDTO
import io.cloudflight.jems.server.user.service.model.assignment.ProjectWithUsers
import io.cloudflight.jems.server.user.service.model.assignment.UpdateProjectUser
import org.springframework.data.domain.Page

fun ProjectWithUsers.toDto() = ProjectUserDTO(
    id = id,
    customIdentifier = customIdentifier,
    acronym = acronym,
    projectStatus = projectStatus.toDTO(),
    assignedUserIds = assignedUserIds,
)

fun Page<ProjectWithUsers>.toDto() = map { it.toDto() }

fun Set<UpdateProjectUserDTO>.toModel() = mapTo(HashSet()) { UpdateProjectUser(
    projectId = it.projectId,
    userIdsToRemove = it.userIdsToRemove,
    userIdsToAdd = it.userIdsToAdd,
) }
