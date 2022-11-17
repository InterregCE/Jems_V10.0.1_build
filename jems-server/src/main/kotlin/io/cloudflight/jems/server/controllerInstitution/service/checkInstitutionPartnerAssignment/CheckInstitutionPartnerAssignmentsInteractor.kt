package io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment

interface CheckInstitutionPartnerAssignmentsInteractor {

    fun checkInstitutionAssignmentsToRemoveForUpdatedPartners(projectId: Long)

    fun checkInstitutionAssignmentsToRemoveForUpdatedInstitution(institutionId: Long)
}
