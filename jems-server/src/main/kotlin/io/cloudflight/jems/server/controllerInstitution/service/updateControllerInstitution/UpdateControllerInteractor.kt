package io.cloudflight.jems.server.controllerInstitution.service.updateControllerInstitution

import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution

interface UpdateControllerInteractor {

    fun updateControllerInstitution(
        institutionId: Long,
        controllerInstitution: UpdateControllerInstitution
    ): ControllerInstitution
}
