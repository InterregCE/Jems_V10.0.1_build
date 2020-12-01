package io.cloudflight.jems.server.programme.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
annotation class CanUpdateProgrammeSetup

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
