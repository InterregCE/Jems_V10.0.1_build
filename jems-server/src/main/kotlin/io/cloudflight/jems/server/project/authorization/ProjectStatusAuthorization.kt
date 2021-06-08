package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_SUBMITTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.SUBMITTED
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
    val projectPersistence: ProjectPersistence,
    val projectService: ProjectService
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

    fun canApproveOrRefuse(projectId: Long): Boolean {
        val project = projectPersistence.getProject(projectId)
        val oldStatus = project.projectStatus.status
        val oldPossibilities = setOf(STEP1_ELIGIBLE, ELIGIBLE, APPROVED_WITH_CONDITIONS)

        if (!isProgrammeUser() && ! isAdmin())
            throw ResourceNotFoundException("project")

        val decisions = if (oldStatus.isInStep2()) project.assessmentStep2 else project.assessmentStep1

        return oldPossibilities.contains(oldStatus) && decisions?.assessmentQuality != null
    }

    fun canApproveWithConditions(projectId: Long): Boolean {
        val oldStatus = projectPersistence.getApplicantAndStatusById(projectId).projectStatus
        return oldStatus.isEligible() && (isProgrammeUser() || isAdmin())
    }

    fun canSetEligibility(projectId: Long): Boolean {
        val project = projectPersistence.getProject(projectId)
        val oldStatus = project.projectStatus.status

        if (!isProgrammeUser() && ! isAdmin())
            throw ResourceNotFoundException("project")

        val decisions = if (oldStatus.isInStep2()) project.assessmentStep2 else project.assessmentStep1

        return oldStatus.isSubmitted() && decisions?.assessmentEligibility != null
    }

    fun canReturnToApplicant(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val oldStatus = project.projectStatus
        val oldPossibilities = setOf(SUBMITTED, ELIGIBLE, APPROVED_WITH_CONDITIONS, APPROVED)

        return oldPossibilities.contains(oldStatus) && (isProgrammeUser() || isAdmin())
    }

    fun canStartSecondStep(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val oldPossibilities = setOf(STEP1_APPROVED_WITH_CONDITIONS, STEP1_APPROVED)

        if (!isProgrammeUser() && ! isAdmin())
            throw ResourceNotFoundException("project")

        return oldPossibilities.contains(project.projectStatus)
    }

    fun canSetQualityAssessment(projectId: Long): Boolean {
        val project = projectPersistence.getProject(projectId)
        val allowedStatuses = listOf(SUBMITTED, ELIGIBLE, STEP1_SUBMITTED, STEP1_ELIGIBLE)

        if (!isProgrammeUser() && ! isAdmin())
            throw ResourceNotFoundException("project")

        val decision = if (project.projectStatus.status.isInStep2()) project.assessmentStep2 else project.assessmentStep1

        return allowedStatuses.contains(project.projectStatus.status) && decision?.assessmentQuality == null
    }

    fun canSetEligibilityAssessment(projectId: Long): Boolean {
        val project = projectPersistence.getProject(projectId)
        val status = project.projectStatus.status

        if (!isProgrammeUser() && ! isAdmin())
            throw ResourceNotFoundException("project")

        val decision = if (status.isInStep2()) project.assessmentStep2 else project.assessmentStep1

        return decision?.assessmentEligibility == null && status.isSubmitted()
    }

}
