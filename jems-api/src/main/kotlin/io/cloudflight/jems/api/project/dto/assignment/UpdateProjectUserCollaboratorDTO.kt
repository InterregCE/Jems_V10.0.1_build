package io.cloudflight.jems.api.project.dto.assignment

data class UpdateProjectUserCollaboratorDTO(
    val userEmail: String,
    val level: ProjectCollaboratorLevelDTO,
)
