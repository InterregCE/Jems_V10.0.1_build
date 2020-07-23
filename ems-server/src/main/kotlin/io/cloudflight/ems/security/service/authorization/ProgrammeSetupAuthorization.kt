package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.security.service.SecurityService
import org.springframework.stereotype.Component

@Component
class ProgrammeSetupAuthorization(
    override val securityService: SecurityService
): Authorization(securityService) {

    fun canAccessSetup(): Boolean {
        return isAdmin() || isProgrammeUser()
    }

}
