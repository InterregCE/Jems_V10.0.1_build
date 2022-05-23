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
@PreAuthorize("authorization.hasPermission('ProjectAssessmentChecklistSelectedRetrieve', #relatedToId)")
annotation class CanViewChecklistAssessmentSelection

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentChecklistSelectedUpdate')")
annotation class CanUpdateChecklistAssessmentSelection

@Component
class ProjectChecklistAuthorization(securityService: SecurityService) : Authorization(securityService) {

    fun canConsolidate(projectId: Long): Boolean =
        hasPermission(UserRolePermission.ProjectAssessmentChecklistConsolidate, projectId)
}

