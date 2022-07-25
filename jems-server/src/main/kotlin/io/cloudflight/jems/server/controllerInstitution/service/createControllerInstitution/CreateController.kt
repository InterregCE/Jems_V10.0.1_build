package io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanCreateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionValidator
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateController(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val controllerInstitutionValidator: ControllerInstitutionValidator
): CreateControllerInteractor {


    @CanCreateControllerInstitution
    @Transactional
    @ExceptionWrapper(UpdateControllerInstitutionException::class)
    override fun createController(controllerInstitution: UpdateControllerInstitution): ControllerInstitution {
        if (controllerInstitution.institutionUsers.isNotEmpty()) {
            controllerInstitutionValidator.validateInstitutionUsers(controllerInstitution.institutionUsers)
        }
        return this.controllerInstitutionPersistence.createControllerInstitution(controllerInstitution)
    }

}
