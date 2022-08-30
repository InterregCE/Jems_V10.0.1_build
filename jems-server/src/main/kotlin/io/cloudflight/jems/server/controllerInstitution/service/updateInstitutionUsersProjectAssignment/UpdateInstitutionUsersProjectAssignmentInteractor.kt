package io.cloudflight.jems.server.controllerInstitution.service.updateInstitutionUsersProjectAssignment

import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment

interface UpdateInstitutionUsersProjectAssignmentInteractor {

     fun updateInstitutionUsersProjectAssignment(
        savedOrUpdatedAssignments: List<InstitutionPartnerAssignment>,
        removedAssignments: List<InstitutionPartnerAssignment>,
        existingAssignmentsBeforeUpdate: List<InstitutionPartnerAssignment>,
    )
}
