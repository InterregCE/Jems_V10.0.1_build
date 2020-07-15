package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.ELIGIBLE
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.INELIGIBLE
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.RETURNED_TO_APPLICANT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.SUBMITTED
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.ProjectService
import org.springframework.stereotype.Component

@Component
class ProjectStatusAuthorization(
    val securityService: SecurityService,
    val projectService: ProjectService
) {

    fun canChangeStatusTo(projectId: Long, newStatus: ProjectApplicationStatus): Boolean {
        val project = projectService.getById(projectId)
        return canChangeStatusTo(project, newStatus)
    }

    fun canChangeStatusTo(project: OutputProject, newStatus: ProjectApplicationStatus): Boolean {
        val oldStatus = project.projectStatus.status

        if (submitted(oldStatus, newStatus))
            return isOwner(project) || isAdmin()

        if (returned(oldStatus, newStatus))
            return isProgrammeUser() || isAdmin()

        if (eligibilityFilled(project, newStatus))
            return isProgrammeUser() || isAdmin()

        return false
    }

    fun submitted(oldStatus: ProjectApplicationStatus, newStatus: ProjectApplicationStatus): Boolean {
        return (oldStatus == DRAFT || oldStatus == RETURNED_TO_APPLICANT) && newStatus == SUBMITTED
    }

    fun returned(oldStatus: ProjectApplicationStatus, newStatus: ProjectApplicationStatus): Boolean {
        val oldPossibilities = setOf(SUBMITTED, ELIGIBLE)
        return oldPossibilities.contains(oldStatus) && newStatus == RETURNED_TO_APPLICANT
    }

    fun eligibilityFilled(project: OutputProject, newStatus: ProjectApplicationStatus): Boolean {
        val newPossibilities = setOf(ELIGIBLE, INELIGIBLE)
        val oldPossibilities = setOf(SUBMITTED, RETURNED_TO_APPLICANT)

        return oldPossibilities.contains(project.projectStatus.status)
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

    fun isOwner(project: OutputProject): Boolean {
        return project.applicant.id == securityService.currentUser?.user?.id
    }

    fun isAdmin(): Boolean {
        return securityService.currentUser?.isAdmin!!
    }

    fun isProgrammeUser(): Boolean {
        return securityService.currentUser?.isProgrammeUser!!
    }

}
