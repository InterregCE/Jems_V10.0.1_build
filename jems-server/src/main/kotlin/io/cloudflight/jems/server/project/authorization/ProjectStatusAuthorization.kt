package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.*
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.Companion.isDraft
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.Companion.isEligible
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.Companion.isSubmitted
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canSubmit(#projectId)")
annotation class CanSubmitApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canReadProject(#projectId) && @projectStatusAuthorization.canApproveOrRefuse(#projectId)")
annotation class CanApproveApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canReadProject(#projectId) && @projectStatusAuthorization.canApproveWithConditions(#projectId)")
annotation class CanApproveApplicationWithConditions

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canReadProject(#projectId) && @projectStatusAuthorization.canApproveOrRefuse(#projectId)")
annotation class CanRefuseApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canReadProject(#projectId) && @projectStatusAuthorization.canSetEligibility(#projectId)")
annotation class CanSetApplicationAsEligible

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canReadProject(#projectId) && @projectStatusAuthorization.canSetEligibility(#projectId)")
annotation class CanSetApplicationAsIneligible

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canReadProject(#projectId) && @projectStatusAuthorization.canReturnToApplicant(#projectId)")
annotation class CanReturnApplicationToApplicant

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canReadProject(#projectId) && @projectStatusAuthorization.canStartSecondStep(#projectId)")
annotation class CanStartSecondStep

@Component
class ProjectStatusAuthorization(
    override val securityService: SecurityService,
    val projectAuthorization: ProjectAuthorization,
    val projectService: ProjectService
) : Authorization(securityService) {

    fun canSubmit(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val oldStatus = project.projectStatus.status

        return (isDraft(oldStatus) || oldStatus == RETURNED_TO_APPLICANT)
            && (isApplicantOwner(project.applicant.id!!) || isAdmin() || hasPermission(UserRolePermission.ProjectSubmission))
    }

    fun canApproveOrRefuse(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val oldStatus = project.projectStatus.status
        val oldPossibilities = setOf(STEP1_ELIGIBLE, ELIGIBLE, APPROVED_WITH_CONDITIONS)

        return oldPossibilities.contains(oldStatus)
            && project.getStep()?.qualityAssessment != null
            && (isProgrammeUser() || isAdmin())
    }

    fun canApproveWithConditions(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val oldStatus = project.projectStatus.status

        return isEligible(oldStatus) && (isProgrammeUser() || isAdmin())
    }

    fun canSetEligibility(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val oldStatus = project.projectStatus.status

        return isSubmitted(oldStatus)
            && project.getStep()?.eligibilityAssessment != null
            && (isProgrammeUser() || isAdmin())
    }

    fun canReturnToApplicant(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val oldStatus = project.projectStatus.status
        val oldPossibilities = setOf(SUBMITTED, ELIGIBLE, APPROVED_WITH_CONDITIONS, APPROVED)

        return oldPossibilities.contains(oldStatus) && (isProgrammeUser() || isAdmin())
    }

    fun canStartSecondStep(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val oldStatus = project.projectStatus.status
        val oldPossibilities = setOf(STEP1_APPROVED_WITH_CONDITIONS, STEP1_APPROVED)

        return oldPossibilities.contains(oldStatus) && (isProgrammeUser() || isAdmin())
    }

    fun canSetQualityAssessment(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val allowedStatuses = listOf(SUBMITTED, ELIGIBLE, STEP1_SUBMITTED, STEP1_ELIGIBLE)

        return project.getStep()?.qualityAssessment == null
            && (isProgrammeUser() || isAdmin())
            && allowedStatuses.contains(project.projectStatus.status)
    }

    fun canSetEligibilityAssessment(projectId: Long): Boolean {
        val project = projectService.getById(projectId)

        return project.getStep()?.eligibilityAssessment == null
            && (isProgrammeUser() || isAdmin())
            && isSubmitted(project.projectStatus.status)
    }

}
