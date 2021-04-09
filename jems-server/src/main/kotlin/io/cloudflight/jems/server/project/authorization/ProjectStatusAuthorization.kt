package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.APPROVED
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.DRAFT
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.ELIGIBLE
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.INELIGIBLE
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.NOT_APPROVED
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.RETURNED_TO_APPLICANT
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.SUBMITTED
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.controller.toDTO
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canChangeStatusTo(#projectId, T(io.cloudflight.jems.server.project.service.application.ApplicationStatus).SUBMITTED)")
annotation class CanSubmitApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canChangeStatusTo(#projectId, T(io.cloudflight.jems.server.project.service.application.ApplicationStatus).APPROVED)")
annotation class CanApproveApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canChangeStatusTo(#projectId, T(io.cloudflight.jems.server.project.service.application.ApplicationStatus).APPROVED_WITH_CONDITIONS)")
annotation class CanApproveApplicationWithConditions

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canChangeStatusTo(#projectId, T(io.cloudflight.jems.server.project.service.application.ApplicationStatus).NOT_APPROVED)")
annotation class CanRefuseApplication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canChangeStatusTo(#projectId, T(io.cloudflight.jems.server.project.service.application.ApplicationStatus).ELIGIBLE)")
annotation class CanSetApplicationAsEligible

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canChangeStatusTo(#projectId, T(io.cloudflight.jems.server.project.service.application.ApplicationStatus).INELIGIBLE)")
annotation class CanSetApplicationAsIneligible

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectStatusAuthorization.canChangeStatusTo(#projectId, T(io.cloudflight.jems.server.project.service.application.ApplicationStatus).RETURNED_TO_APPLICANT)")
annotation class CanReturnApplicationToApplicant

@Component
class ProjectStatusAuthorization(
    override val securityService: SecurityService,
    val projectAuthorization: ProjectAuthorization,
    val projectService: ProjectService
) : Authorization(securityService) {

    fun canChangeStatusTo(projectId: Long, newStatus: ApplicationStatus): Boolean {
        return projectAuthorization.canReadProject(projectId)
            && canChangeStatusTo(projectService.getById(projectId), newStatus.toDTO())
    }

    fun canChangeStatusTo(projectDetailDTO: ProjectDetailDTO, newStatus: ApplicationStatusDTO): Boolean {
        val oldStatus = projectDetailDTO.projectStatus.status

        if (submitted(oldStatus, newStatus)) {
            return (isApplicantOwner(projectDetailDTO.applicant.id!!) || isAdmin())
        }

        if (returned(oldStatus, newStatus))
            return isProgrammeUser() || isAdmin()

        if (eligibilityFilled(projectDetailDTO, newStatus))
            return isProgrammeUser() || isAdmin()

        if (fundingFilled(projectDetailDTO, newStatus) || fundingChanged(projectDetailDTO, newStatus))
            return isProgrammeUser() || isAdmin()

        return false
    }

    private fun submitted(oldStatus: ApplicationStatusDTO, newStatus: ApplicationStatusDTO): Boolean {
        return (oldStatus == DRAFT || oldStatus == RETURNED_TO_APPLICANT) && newStatus == SUBMITTED
    }

    private fun returned(oldStatus: ApplicationStatusDTO, newStatus: ApplicationStatusDTO): Boolean {
        val oldPossibilities = setOf(SUBMITTED, ELIGIBLE, APPROVED_WITH_CONDITIONS, APPROVED)
        return oldPossibilities.contains(oldStatus) && newStatus == RETURNED_TO_APPLICANT
    }

    private fun fundingFilled(projectDetailDTO: ProjectDetailDTO, newStatus: ApplicationStatusDTO): Boolean {
        val newPossibilities = setOf(APPROVED, APPROVED_WITH_CONDITIONS, NOT_APPROVED)
        val oldStatus = projectDetailDTO.projectStatus.status

        return oldStatus == ELIGIBLE
            && newPossibilities.contains(newStatus)
            && projectDetailDTO.qualityAssessment != null
    }

    private fun fundingChanged(projectDetailDTO: ProjectDetailDTO, newStatus: ApplicationStatusDTO): Boolean {
        val newPossibilities = setOf(APPROVED, NOT_APPROVED)
        val oldStatus = projectDetailDTO.projectStatus.status

        return oldStatus == APPROVED_WITH_CONDITIONS
            && newPossibilities.contains(newStatus)
    }

    private fun eligibilityFilled(projectDetailDTO: ProjectDetailDTO, newStatus: ApplicationStatusDTO): Boolean {
        val newPossibilities = setOf(ELIGIBLE, INELIGIBLE)
        val oldStatus = projectDetailDTO.projectStatus.status

        return oldStatus == SUBMITTED
            && newPossibilities.contains(newStatus)
            && projectDetailDTO.eligibilityAssessment != null
    }

    fun canSetQualityAssessment(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val allowedStatuses = listOf(SUBMITTED, ELIGIBLE)

        return project.qualityAssessment == null
            && (isProgrammeUser() || isAdmin())
            && allowedStatuses.contains(project.projectStatus.status)
    }

    fun canSetEligibilityAssessment(projectId: Long): Boolean {
        val project = projectService.getById(projectId)

        return project.eligibilityAssessment == null
            && (isProgrammeUser() || isAdmin())
            && project.projectStatus.status == SUBMITTED
    }

}
