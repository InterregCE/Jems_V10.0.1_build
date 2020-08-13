package io.cloudflight.ems.nuts.authorization

import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.authorization.Authorization
import org.springframework.stereotype.Component

@Component
class ProgrammeNutsAuthorization(
    override val securityService: SecurityService
): Authorization(securityService) {

    fun canSetupNuts(): Boolean {
        return isAdmin() || isProgrammeUser()
    }

}
