package io.cloudflight.jems.server.controllerInstitution.service.model

data class ControllerInstitutionUser (
    val institutionId: Long,
    val userId: Long,
    val userEmail: String,
    val accessLevel: UserInstitutionAccessLevel
)
