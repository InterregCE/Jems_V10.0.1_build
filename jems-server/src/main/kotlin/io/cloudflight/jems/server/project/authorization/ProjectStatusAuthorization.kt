package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.hasPermissionOrIsEditCollaborator('ProjectSubmission', #projectId)")
annotation class CanSubmitApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.hasPermissionOrIsViewCollaborator('ProjectCheckApplicationForm', #projectId)")
annotation class CanCheckApplicationForm

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusReturnToApplicant', #projectId)")
annotation class CanReturnApplicationToApplicant

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusDecideApproved', #projectId)")
annotation class CanApproveApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusDecideApprovedWithConditions', #projectId)")
annotation class CanApproveApplicationWithConditions

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusDecideNotApproved', #projectId)")
annotation class CanRefuseApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusDecideEligible', #projectId)")
annotation class CanSetApplicationAsEligible

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusDecideIneligible', #projectId)")
annotation class CanSetApplicationAsIneligible

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStartStepTwo', #projectId)")
annotation class CanStartSecondStep

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusDecisionRevert', #projectId)")
annotation class CanRevertDecision

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectModificationView', #projectId)")
annotation class CanRetrieveProjectModifications

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectSetToContracted', #projectId)")
annotation class CanSetProjectToContracted

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectContractingView')")
annotation class CanRetrieveProjectContractingMonitoring

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectOpenModification')")
annotation class CanOpenModification

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusDecideModificationApproved', #projectId)")
annotation class CanApproveModification

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectStatusDecideModificationNotApproved', #projectId)")
annotation class CanRejectModification

@Component
class ProjectStatusAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
) : Authorization(securityService) {

    fun hasPermissionOrIsViewCollaborator(permission: UserRolePermission, projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val isOwner = isActiveUserIdEqualToOneOf(project.getUserIdsWithViewLevel())

        if (isOwner || hasPermissionForProject(permission, projectId))
            return true
        else
            throw ResourceNotFoundException("project")
    }
    fun hasPermissionOrIsEditCollaborator(permission: UserRolePermission, projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val isOwner = isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())

        if (isOwner || hasPermissionForProject(permission, projectId))
            return true
        else
            throw ResourceNotFoundException("project")
    }

}
