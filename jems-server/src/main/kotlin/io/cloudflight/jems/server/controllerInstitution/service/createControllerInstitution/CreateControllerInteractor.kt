package io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution

import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution

interface CreateControllerInteractor {

    fun createController(controllerInstitution: UpdateControllerInstitution): ControllerInstitution

}
