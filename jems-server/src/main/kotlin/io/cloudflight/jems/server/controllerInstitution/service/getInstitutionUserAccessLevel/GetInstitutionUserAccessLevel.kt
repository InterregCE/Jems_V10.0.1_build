package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUserAccessLevel

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetInstitutionUserAccessLevel(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val securityService: SecurityService
) : GetInstitutionUserAccessLevelInteractor {

    @ExceptionWrapper(GetInstitutionUserAccessLevelException::class)
    @Transactional(readOnly = true)
    override fun getControllerUserAccessLevelForPartner(partnerId: Long): UserInstitutionAccessLevel? =
        controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(
            securityService.getUserIdOrThrow(),
            partnerId
        )
}
