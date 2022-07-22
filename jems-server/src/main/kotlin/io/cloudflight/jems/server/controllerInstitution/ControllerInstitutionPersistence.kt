package io.cloudflight.jems.server.controllerInstitution

import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional


interface ControllerInstitutionPersistence {

    fun getControllerInstitutions(pageable: Pageable): Page<ControllerInstitutionList>

    fun getControllerInstitutionById(controllerInstitutionId: Long): ControllerInstitution

    fun getControllerInstitutionsByUserId(userId: Long, pageable: Pageable): Page<ControllerInstitutionList>

    fun getInstitutionUserByInstitutionIdAndUserId(institutionId: Long, userId: Long): Optional<ControllerInstitutionUser>

    fun createControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution

    fun updateControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution
}
