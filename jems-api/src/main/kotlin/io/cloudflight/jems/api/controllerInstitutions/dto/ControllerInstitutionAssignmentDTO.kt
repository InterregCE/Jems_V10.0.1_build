package io.cloudflight.jems.api.controllerInstitutions.dto

data class ControllerInstitutionAssignmentDTO(
    val assignmentsToAdd: List<InstitutionPartnerAssignmentDTO>,
    val assignmentsToRemove: List<InstitutionPartnerAssignmentDTO>
)

