package io.cloudflight.jems.api.project.dto

data class UpdateProjectUserDTO(
    val projectId: Long,
    val userIdsToAdd: Set<Long>,
    val userIdsToRemove: Set<Long> = emptySet()
 )
