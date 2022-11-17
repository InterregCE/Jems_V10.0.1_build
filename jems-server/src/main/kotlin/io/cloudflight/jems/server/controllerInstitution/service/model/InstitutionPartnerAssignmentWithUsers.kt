package io.cloudflight.jems.server.controllerInstitution.service.model

data class InstitutionPartnerAssignmentWithUsers(
    val institutionId: Long,
    val userId: Long,
    val partnerProjectId: Long
)
