package io.cloudflight.jems.server.user.service.model.assignment

data class UpdateProjectUser(
    val projectId: Long,
    val userIdsToAdd: Set<Long>,
    val userIdsToRemove: Set<Long> = emptySet()
)
