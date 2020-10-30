package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.security.service.SecurityService
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerService
import io.cloudflight.jems.server.security.service.authorization.Authorization
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectPartnerAuthorization.canUpdateProjectPartner(#partnerId)")
annotation class CanUpdateProjectPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectPartnerAuthorization.canReadProjectPartner(#partnerId)")
annotation class CanReadProjectPartner

@Component
class ProjectPartnerAuthorization(
    override val securityService: SecurityService,
    val projectPartnerService: ProjectPartnerService,
    val projectAuthorization: ProjectAuthorization
): Authorization(securityService) {

    fun canReadProjectPartner(partnerId: Long): Boolean {
        return projectAuthorization.canReadProject(projectPartnerService.getProjectIdForPartnerId(partnerId))
    }

    fun canUpdateProjectPartner(partnerId: Long): Boolean {
        return projectAuthorization.canUpdateProject(projectPartnerService.getProjectIdForPartnerId(partnerId))
    }

}
