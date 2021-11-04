package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.ProjectUserDTO
import io.cloudflight.jems.api.project.dto.UpdateProjectUserDTO
import io.cloudflight.jems.server.user.service.model.ProjectWithUsers
import io.cloudflight.jems.server.user.service.model.UpdateProjectUser
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
