package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectPartnerAuthorization.canOwnerUpdatePartner(#partnerId)")
annotation class CanUpdateProjectPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieve') || @projectPartnerAuthorization.isUserOwnerOfProjectOfPartner(#partnerId, #version)")
annotation class CanRetrieveProjectPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectPartnerAuthorization.canOwnerUpdatePartner(#projectPartner.id)")
annotation class CanUpdateProjectPartnerBase

@Component
class ProjectPartnerAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
    val partnerPersistence: PartnerPersistence
) : Authorization(securityService) {

    fun isUserOwnerOfProjectOfPartner(partnerId: Long, version: String?): Boolean =
        isActiveUserIdEqualTo(userId = getProjectFromPartnerId(partnerId, version).applicantId)

    fun canOwnerUpdatePartner(partnerId: Long): Boolean {
        val project = getProjectFromPartnerId(partnerId)
        return project.projectStatus.hasNotBeenSubmittedYet() && isActiveUserIdEqualTo(project.applicantId)
    }

    private fun getProjectFromPartnerId(partnerId: Long, version: String? = null): ProjectApplicantAndStatus {
        return projectPersistence.getApplicantAndStatusById(
            partnerPersistence.getProjectIdForPartnerId(partnerId, version)
        )
    }

}
