package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canSubmit(#projectId)")
annotation class CanSubmitApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectStatusReturnToApplicant')")
annotation class CanReturnApplicationToApplicant

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectStatusDecideApproved')")
annotation class CanApproveApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectStatusDecideApprovedWithConditions')")
annotation class CanApproveApplicationWithConditions

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectStatusDecideNotApproved')")
annotation class CanRefuseApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectStatusDecideEligible')")
annotation class CanSetApplicationAsEligible

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectStatusDecideIneligible')")
annotation class CanSetApplicationAsIneligible

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectStartStepTwo')")
annotation class CanStartSecondStep

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectStatusDecisionRevert')")
annotation class CanRevertDecision

@Component
class ProjectStatusAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
) : Authorization(securityService) {

    fun canSubmit(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val isOwner = isActiveUserIdEqualTo(userId = project.applicantId)

        if (isOwner || hasPermission(UserRolePermission.ProjectSubmission))
            return project.projectStatus.isDraftOrReturned()
        else if (hasPermission(UserRolePermission.ProjectRetrieve))
            return false
        else
            throw ResourceNotFoundException("project")
    }

}
