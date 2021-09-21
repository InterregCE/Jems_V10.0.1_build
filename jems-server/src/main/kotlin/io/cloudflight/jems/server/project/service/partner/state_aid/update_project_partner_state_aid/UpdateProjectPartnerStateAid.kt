package io.cloudflight.jems.server.project.service.partner.state_aid.update_project_partner_state_aid

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartnerStateAid(
    private val persistence: PartnerPersistence,
    private val generalValidator: GeneralValidatorService
) : UpdateProjectPartnerStateAidInteractor {

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerStateAidException::class)
    override fun updatePartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAid): ProjectPartnerStateAid {
        validatePartnerStateAid(stateAid)
        return persistence.updatePartnerStateAid(partnerId, stateAid)
    }

    private fun validatePartnerStateAid(stateAid: ProjectPartnerStateAid) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(stateAid.justification1, 1000, "justification1"),
            generalValidator.maxLength(stateAid.justification2, 1000, "justification2"),
            generalValidator.maxLength(stateAid.justification3, 1000, "justification3"),
            generalValidator.maxLength(stateAid.justification4, 1000, "justification4"),
        )
}
