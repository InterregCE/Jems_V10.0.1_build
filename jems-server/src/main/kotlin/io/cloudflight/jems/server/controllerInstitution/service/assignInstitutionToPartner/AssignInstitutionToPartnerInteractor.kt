package io.cloudflight.jems.server.controllerInstitution.service.assignInstitutionToPartner

import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment

interface AssignInstitutionToPartnerInteractor {

    fun assignInstitutionToPartner(institutionPartnerAssignments: ControllerInstitutionAssignment): List<InstitutionPartnerAssignment>
}
