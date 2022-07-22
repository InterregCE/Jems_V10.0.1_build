package io.cloudflight.jems.api.controllerInstitutions.dto

data class ControllerInstitutionUserDTO (
    val institutionId: Long,
    val userEmail: String,
    val userId: Long,
    val accessLevel: UserInstitutionAccessLevelDTO
)
