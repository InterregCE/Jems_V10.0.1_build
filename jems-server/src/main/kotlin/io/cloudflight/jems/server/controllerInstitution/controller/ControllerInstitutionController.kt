package io.cloudflight.jems.server.controllerInstitution.controller

import io.cloudflight.jems.api.controllerInstitutions.ControllerInstitutionApi
import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionListDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.UpdateControllerInstitutionDTO
import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.CreateControllerInteractor
import io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitution.GetControllerInteractor
import io.cloudflight.jems.server.controllerInstitution.service.updateControllerInstitution.UpdateControllerInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerInstitutionController(
    private val getControllerInteractor: GetControllerInteractor,
    private val createControllerInteractor: CreateControllerInteractor,
    private val updateControllerInstitution: UpdateControllerInteractor
): ControllerInstitutionApi {

    override fun getControllers(pageable: Pageable): Page<ControllerInstitutionListDTO> =
        getControllerInteractor.getControllers(pageable).toListDto()

    override fun getControllerInstitutionById(institutionId: Long): ControllerInstitutionDTO =
        getControllerInteractor.getControllerInstitutionById(institutionId).toDto()


    override fun createController(controllerData: UpdateControllerInstitutionDTO): ControllerInstitutionDTO =
        createControllerInteractor.createController(controllerData.toModel()).toDto()

    override fun updateController(
        institutionId: Long,
        controllerData: UpdateControllerInstitutionDTO
    ) =
        updateControllerInstitution.updateControllerInstitution(institutionId, controllerData.toModel()).toDto()

}


