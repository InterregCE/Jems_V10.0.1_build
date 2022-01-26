package io.cloudflight.jems.api.project.dto.assignment

data class UpdatePartnerUserCollaboratorDTO(
    val userEmail: String,
    val level: PartnerCollaboratorLevelDTO,
)
