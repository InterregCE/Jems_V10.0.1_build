package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionPartnerAssignment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanViewInstitutionAssignments
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetInstitutionPartnerAssignment(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
): GetInstitutionPartnerAssignmentInteractor {

    @CanViewInstitutionAssignments
    @ExceptionWrapper(GetInstitutionPartnerAssignmentException::class)
    @Transactional(readOnly = true)
    override fun getInstitutionPartnerAssignments(pageable: Pageable): Page<InstitutionPartnerDetails> =
        controllerInstitutionPersistence.getInstitutionPartnerAssignments(pageable)
}
