package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectPartnerAuthorization.canUpdatePartner(#partnerId)")
annotation class CanUpdateProjectPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFormRetrieve') || @projectPartnerAuthorization.isOwnerOfPartner(#partnerId, #version)")
annotation class CanRetrieveProjectPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFormRetrieve') || hasAuthority('ProjectFileApplicationRetrieve') || @projectAuthorization.isUserOwnerOfProject(#projectId)")
annotation class CanRetrieveProjectPartnerSummaries

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectPartnerAuthorization.canUpdatePartner(#projectPartner.id)")
annotation class CanUpdateProjectPartnerBase

@Component
class ProjectPartnerAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
    val partnerPersistence: PartnerPersistence
) : Authorization(securityService) {

    fun isOwnerOfPartner(partnerId: Long, version: String? = null): Boolean {
        val isOwner = isActiveUserIdEqualTo(userId = getProjectFromPartnerId(partnerId, version).applicantId)
        if (isOwner)
            return true
        throw ResourceNotFoundException("partner") // should be same exception as if entity not found
    }

    fun canUpdatePartner(partnerId: Long): Boolean {
        val project = getProjectFromPartnerId(partnerId)
        val canSeePartner = hasPermission(UserRolePermission.ProjectFormUpdate) || isActiveUserIdEqualTo(project.applicantId)
        if (canSeePartner)
            return project.projectStatus.hasNotBeenSubmittedYet()
        throw ResourceNotFoundException("partner")
    }

    private fun getProjectFromPartnerId(partnerId: Long, version: String? = null): ProjectApplicantAndStatus {
        return projectPersistence.getApplicantAndStatusById(
            partnerPersistence.getProjectIdForPartnerId(partnerId, version)
        )
    }

}
