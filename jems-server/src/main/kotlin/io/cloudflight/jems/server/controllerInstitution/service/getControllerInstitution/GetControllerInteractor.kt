package io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitution

import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetControllerInteractor {

    fun getControllers(pageable: Pageable): Page<ControllerInstitutionList>

    fun getControllerInstitutionById(institutionId: Long): ControllerInstitution

}
