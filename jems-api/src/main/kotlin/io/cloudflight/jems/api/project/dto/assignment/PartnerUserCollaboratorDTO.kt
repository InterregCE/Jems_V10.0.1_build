package io.cloudflight.jems.api.project.dto.assignment

data class PartnerUserCollaboratorDTO(
    val partnerId: Long,
    val userId: Long,
    val userEmail: String,
    val level: PartnerCollaboratorLevelDTO,
)
