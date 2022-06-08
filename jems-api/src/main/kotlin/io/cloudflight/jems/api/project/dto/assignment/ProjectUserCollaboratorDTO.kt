package io.cloudflight.jems.api.project.dto.assignment

data class ProjectUserCollaboratorDTO(
    val userId: Long,
    val userEmail: String,
    val level: ProjectCollaboratorLevelDTO,
)
