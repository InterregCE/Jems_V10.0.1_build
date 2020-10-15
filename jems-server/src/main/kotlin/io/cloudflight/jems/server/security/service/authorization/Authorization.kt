package io.cloudflight.jems.server.security.service.authorization

import io.cloudflight.jems.server.security.APPLICANT_USER
import io.cloudflight.jems.server.security.service.SecurityService

abstract class Authorization(
    open val securityService: SecurityService
) {

    fun isAdmin(): Boolean =
        securityService.currentUser?.isAdmin!!

    fun isProgrammeUser(): Boolean =
        securityService.currentUser?.isProgrammeUser!!

    fun isApplicantUser(): Boolean =
        securityService.currentUser?.hasRole(APPLICANT_USER)!!

    protected fun isApplicantOwner(applicantId: Long): Boolean =
        isApplicantUser() && applicantId == securityService.currentUser?.user?.id

    protected fun isApplicantNotOwner(applicantId: Long): Boolean =
        isApplicantUser() && applicantId != securityService.currentUser?.user?.id

}
