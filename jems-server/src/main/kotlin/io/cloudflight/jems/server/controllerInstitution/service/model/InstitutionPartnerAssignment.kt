package io.cloudflight.jems.server.controllerInstitution.service.model

data class InstitutionPartnerAssignment(
    val institutionId: Long,
    val partnerId: Long,
    var partnerProjectId: Long
)
