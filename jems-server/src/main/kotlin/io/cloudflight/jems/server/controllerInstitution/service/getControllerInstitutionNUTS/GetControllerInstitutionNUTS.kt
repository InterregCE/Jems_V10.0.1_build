package io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitutionNUTS

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.authorization.CanRetrieveControllerInstitutions
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.nuts.service.NutsService
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service

@Service
class GetControllerInstitutionNUTS(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val nutsService: NutsService,
    private val securityService: SecurityService
) : GetControllerInstitutionNUTSInteractor {

    @CanRetrieveControllerInstitutions
    @ExceptionWrapper(GetControllerInstitutionNUTSException::class)
    override fun getAvailableRegionsForCurrentUser(): List<OutputNuts> =
        if (securityService.currentUser?.hasPermission(UserRolePermission.AssignmentsUnlimited) == true)
            nutsService.getNuts()
        else
            controllerInstitutionPersistence.getNutsAvailableForUser(userId = securityService.getUserIdOrThrow())

}
