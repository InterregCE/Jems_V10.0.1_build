package io.cloudflight.jems.server.user.service.model

data class UpdateProjectUser(
    val projectId: Long,
    val userIdsToAdd: Set<Long>,
    val userIdsToRemove: Set<Long> = emptySet()
)
