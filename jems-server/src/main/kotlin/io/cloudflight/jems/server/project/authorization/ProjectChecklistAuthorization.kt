package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@authorization.hasPermission('ProjectAssessmentChecklistUpdate', #createCheckList.relatedToId)")
annotation class CanCreateChecklistAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentChecklistUpdate')")
annotation class CanUpdateChecklistAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentChecklistUpdate')")
annotation class CanDeleteChecklistAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectChecklistAuthorization.hasPermissionOrAsController('ProjectAssessmentChecklistSelectedRetrieve', #relatedToId)")
annotation class CanViewChecklistAssessmentSelection

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectChecklistAuthorization.hasPermissionOrAsController('ProjectAssessmentChecklistSelectedRetrieve', #relatedToId) || " +
    "@projectChecklistAuthorization.hasPermissionOrAsController('ProjectAssessmentChecklistUpdate', #relatedToId)")
annotation class CanViewChecklistAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@authorization.hasPermission('ProjectAssessmentChecklistSelectedUpdate')")
annotation class CanUpdateChecklistAssessmentSelection

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentChecklistUpdate')")
annotation class CanCloneChecklistInstance

@Component
class ProjectChecklistAuthorization(
    override val securityService: SecurityService,
    val authorizationUtilService: AuthorizationUtilService
) : Authorization(securityService) {

    fun canConsolidate(projectId: Long): Boolean =
        hasPermission(UserRolePermission.ProjectAssessmentChecklistConsolidate, projectId)

    fun hasPermissionOrAsController(permission: UserRolePermission, projectId: Long): Boolean =
        hasPermission(permission, projectId) || authorizationUtilService.hasPermissionAsController(permission, projectId)
}
