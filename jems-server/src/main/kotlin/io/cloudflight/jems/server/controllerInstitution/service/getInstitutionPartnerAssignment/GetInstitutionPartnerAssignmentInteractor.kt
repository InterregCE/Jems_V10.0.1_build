package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionPartnerAssignment

import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetInstitutionPartnerAssignmentInteractor {

    fun getInstitutionPartnerAssignments(pageable: Pageable): Page<InstitutionPartnerDetails>
}
