package io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitution

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanRetrieveControllerInstitutions
import io.cloudflight.jems.server.controllerInstitution.authorization.CanViewControllerInstitutionDetails
import io.cloudflight.jems.server.controllerInstitution.authorization.ControllerInstitutionAuthorization
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetController(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val controllerInstitutionAuthorization: ControllerInstitutionAuthorization,
    private val securityService: SecurityService,
): GetControllerInteractor {

    @CanRetrieveControllerInstitutions
    @ExceptionWrapper(GetControllerInstitutionException::class)
    @Transactional(readOnly = true)
    override fun getControllers(pageable: Pageable): Page<ControllerInstitutionList> {
        return if (controllerInstitutionAuthorization.hasPermission(UserRolePermission.InstitutionsUnlimited)) {
            controllerInstitutionPersistence.getControllerInstitutions(pageable)
        } else {
            val currentUserId = securityService.getUserIdOrThrow()
            controllerInstitutionPersistence.getControllerInstitutionsByUserId(currentUserId, pageable)
        }
    }

    @CanViewControllerInstitutionDetails
    @ExceptionWrapper(GetControllerInstitutionException::class)
    override fun getControllerInstitutionById(institutionId: Long): ControllerInstitution  {
        return controllerInstitutionPersistence.getControllerInstitutionById(institutionId)
    }

}
