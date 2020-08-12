package io.cloudflight.ems.project.authorization

import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.ELIGIBLE
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.INELIGIBLE
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.APPROVED
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.APPROVED_WITH_CONDITIONS
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.NOT_APPROVED
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.RETURNED_TO_APPLICANT
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.SUBMITTED
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.project.service.ProjectService
import io.cloudflight.ems.security.service.authorization.Authorization
import org.springframework.stereotype.Component

@Component
class ProjectStatusAuthorization(
    override val securityService: SecurityService,
    val projectAuthorization: ProjectAuthorization,
    val projectService: ProjectService
): Authorization(securityService) {

    fun canChangeStatusTo(projectId: Long, newStatus: ProjectApplicationStatus): Boolean {
        return projectAuthorization.canReadProject(projectId)
            && canChangeStatusTo(projectService.getById(projectId), newStatus)
    }

    fun canChangeStatusTo(project: OutputProject, newStatus: ProjectApplicationStatus): Boolean {
        val oldStatus = project.projectStatus.status

        if (submitted(oldStatus, newStatus))
            return isOwner(project) || isAdmin()

        if (returned(oldStatus, newStatus))
            return isProgrammeUser() || isAdmin()

        if (eligibilityFilled(project, newStatus))
            return isProgrammeUser() || isAdmin()

        if (fundingFilled(project, newStatus) || fundingChanged(project, newStatus))
            return isProgrammeUser() || isAdmin()

        return false
    }

    private fun submitted(oldStatus: ProjectApplicationStatus, newStatus: ProjectApplicationStatus): Boolean {
        return (oldStatus == DRAFT || oldStatus == RETURNED_TO_APPLICANT) && newStatus == SUBMITTED
    }

    private fun returned(oldStatus: ProjectApplicationStatus, newStatus: ProjectApplicationStatus): Boolean {
        val oldPossibilities = setOf(SUBMITTED, ELIGIBLE, APPROVED_WITH_CONDITIONS, APPROVED)
        return oldPossibilities.contains(oldStatus) && newStatus == RETURNED_TO_APPLICANT
    }

    private fun fundingFilled(project: OutputProject, newStatus: ProjectApplicationStatus): Boolean {
        val newPossibilities = setOf(APPROVED, APPROVED_WITH_CONDITIONS, NOT_APPROVED)
        val oldStatus = project.projectStatus.status

        return oldStatus == ELIGIBLE
            && newPossibilities.contains(newStatus)
            && project.qualityAssessment != null
    }

    private fun fundingChanged(project: OutputProject, newStatus: ProjectApplicationStatus): Boolean {
        val newPossibilities = setOf(APPROVED, NOT_APPROVED)
        val oldStatus = project.projectStatus.status

        return oldStatus == APPROVED_WITH_CONDITIONS
            && newPossibilities.contains(newStatus)
    }

    private fun eligibilityFilled(project: OutputProject, newStatus: ProjectApplicationStatus): Boolean {
        val newPossibilities = setOf(ELIGIBLE, INELIGIBLE)
        val oldStatus = project.projectStatus.status

        return oldStatus == SUBMITTED
                && newPossibilities.contains(newStatus)
                && project.eligibilityAssessment != null
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
