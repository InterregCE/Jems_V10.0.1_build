package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectPartnerAuthorization.canOwnerUpdatePartner(#partnerId)")
annotation class CanUpdateProjectPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieve') || @projectPartnerAuthorization.isUserOwnerOfPartner(#partnerId)")
annotation class CanRetrieveProjectPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectPartnerAuthorization.canOwnerUpdatePartner(#projectPartner.id)")
annotation class CanUpdateProjectPartnerBase

@Component
class ProjectPartnerAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
) : Authorization(securityService) {

    fun isUserOwnerOfPartner(partnerId: Long): Boolean =
        isActiveUserIdEqualTo(userId = getProjectFromPartnerId(partnerId).applicantId)

    fun canOwnerUpdatePartner(partnerId: Long): Boolean {
        val project = getProjectFromPartnerId(partnerId)
        return project.projectStatus.hasNotBeenSubmittedYet() && isActiveUserIdEqualTo(project.applicantId)
    }

    private fun getProjectFromPartnerId(partnerId: Long): ProjectApplicantAndStatus {
        return projectPersistence.getApplicantAndStatusById(projectPersistence.getProjectIdForPartner(partnerId))
    }

}
