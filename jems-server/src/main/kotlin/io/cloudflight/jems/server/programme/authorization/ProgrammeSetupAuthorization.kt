package io.cloudflight.jems.server.programme.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('AuditRetrieve')")
annotation class CanRetrieveAuditLog

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProgrammeSetupUpdate')")
annotation class CanUpdateProgrammeSetup

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@programmeSetupAuthorization.canReadProgrammeSetup()")
annotation class CanRetrieveProgrammeSetup

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@programmeSetupAuthorization.canReadProgrammeSetup()")
annotation class CanRetrieveNuts

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProgrammeDataExportRetrieve')")
annotation class CanExportProgrammeData

@Component
class ProgrammeSetupAuthorization(
    override val securityService: SecurityService
): Authorization(securityService) {

    /**
     * currently applicant users need programme setup data as well for filling in the form,
     * that's why this is always true (core logic).
     */
    fun canReadProgrammeSetup() = true

}
