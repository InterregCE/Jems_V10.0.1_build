package io.cloudflight.jems.server.controllerInstitution.service.updateControllerInstitution

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanUpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionValidator
import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.UpdateControllerInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateController(
    private val persistence: ControllerInstitutionPersistence,
    private val controllerInstitutionValidator: ControllerInstitutionValidator
): UpdateControllerInteractor {

    @CanUpdateControllerInstitution
    @Transactional
    @ExceptionWrapper(UpdateControllerInstitutionException::class)
    override fun updateControllerInstitution(institutionId: Long, controllerInstitution: UpdateControllerInstitution): ControllerInstitution {
        if (controllerInstitution.institutionUsers.isNotEmpty()) {
            controllerInstitutionValidator.validateInstitutionUsers(controllerInstitution.institutionUsers)
        }
        return persistence.updateControllerInstitution(controllerInstitution)
    }

}
