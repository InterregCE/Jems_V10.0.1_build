package io.cloudflight.jems.server.programme.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import org.springframework.stereotype.Component

@Component
class ProgrammeSetupAuthorization(
    override val securityService: SecurityService
): Authorization(securityService) {

    fun canAccessSetup(): Boolean {
        return isAdmin() || isProgrammeUser()
    }

    fun canReadIndicators(): Boolean {
        return canAccessSetup() || isApplicantUser()
    }

}
