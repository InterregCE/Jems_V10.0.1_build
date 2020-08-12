package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.service.SecurityService

abstract class Authorization(
    open val securityService: SecurityService
) {

    fun isAdmin(): Boolean =
        securityService.currentUser?.isAdmin!!

    fun isProgrammeUser(): Boolean =
        securityService.currentUser?.isProgrammeUser!!

    fun isApplicantUser(): Boolean =
        securityService.currentUser?.hasRole(APPLICANT_USER)!!

    protected fun isOwner(project: OutputProject): Boolean =
        project.applicant.id == securityService.currentUser?.user?.id

}
