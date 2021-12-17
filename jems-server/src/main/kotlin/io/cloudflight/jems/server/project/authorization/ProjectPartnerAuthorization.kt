package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormUpdate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectPartnerAuthorization.canUpdatePartner(#partnerId)")
annotation class CanUpdateProjectPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectPartnerAuthorization.canRetrievePartner(#partnerId, #version)")
annotation class CanRetrieveProjectPartner

@Retention(AnnotationRetention.RUNTIME)
// ProjectFileApplicationRetrieve is temporary hack because of broken File Upload screen,
// where people needs to see partners even when they cannot see project
@PreAuthorize("@projectAuthorization.hasPermission('ProjectFormRetrieve', #projectId) || @projectAuthorization.hasPermission('ProjectFileApplicationRetrieve', #projectId) || @projectAuthorization.isUserViewCollaboratorForProjectOrThrow(#projectId)")
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

    fun canUpdatePartner(partnerId: Long): Boolean {
        val project = getProjectFromPartnerId(partnerId)
        val canUpdatePartner = hasPermissionForProject(ProjectFormUpdate, projectId = project.projectId)
            || isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())
        if (canUpdatePartner)
            return project.projectStatus.canBeModified()
        throw ResourceNotFoundException("partner")
    }

    fun canRetrievePartner(partnerId: Long, version: String? = null) : Boolean {
        val project = getProjectFromPartnerId(partnerId, version)
        val canRetrievePartner = hasPermissionForProject(ProjectFormRetrieve, projectId = project.projectId)
            || isActiveUserIdEqualToOneOf(project.getUserIdsWithViewLevel())
        if (canRetrievePartner)
            return true
        throw ResourceNotFoundException("partner")
    }

    private fun getProjectFromPartnerId(partnerId: Long, version: String? = null): ProjectApplicantAndStatus {
        return projectPersistence.getApplicantAndStatusById(
            partnerPersistence.getProjectIdForPartnerId(partnerId, version)
        )
    }

}
