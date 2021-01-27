package io.cloudflight.jems.server.programme.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
annotation class CanUpdateProgrammeSetup

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@programmeSetupAuthorization.canReadIndicators()")
annotation class CanReadIndicators

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@programmeSetupAuthorization.canReadNuts()")
annotation class CanReadNuts

@Component
class ProgrammeSetupAuthorization(
    override val securityService: SecurityService
): Authorization(securityService) {

    fun canAccessSetup() = isAdmin() || isProgrammeUser()

    fun canReadIndicators() = canAccessSetup() || isApplicantUser()

    fun canReadNuts() = canAccessSetup() || isApplicantUser()

}
