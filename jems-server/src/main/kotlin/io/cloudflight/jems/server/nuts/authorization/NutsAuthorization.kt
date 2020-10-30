package io.cloudflight.jems.server.nuts.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import org.springframework.stereotype.Component

@Component
class NutsAuthorization(
    override val securityService: SecurityService
): Authorization(securityService) {

    fun canSetupNuts(): Boolean {
        return isAdmin() || isProgrammeUser()
    }

}
