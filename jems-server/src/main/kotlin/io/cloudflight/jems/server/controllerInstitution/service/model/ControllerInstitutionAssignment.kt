package io.cloudflight.jems.server.controllerInstitution.service.model

data class ControllerInstitutionAssignment(
    val assignmentsToAdd: List<InstitutionPartnerAssignment>,
    val assignmentsToRemove: List<InstitutionPartnerAssignment>
)
