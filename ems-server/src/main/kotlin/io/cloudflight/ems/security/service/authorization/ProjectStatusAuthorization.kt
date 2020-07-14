package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.*
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.PROGRAMME_USER
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
        return (oldStatus == DRAFT && newStatus == SUBMITTED)
            || (oldStatus == RETURNED_TO_APPLICANT && newStatus == RESUBMITTED)
    }

    fun returned(oldStatus: ProjectApplicationStatus, newStatus: ProjectApplicationStatus): Boolean {
        val oldPossibilities = setOf(SUBMITTED, RESUBMITTED)

        return oldPossibilities.contains(oldStatus) && newStatus == RETURNED_TO_APPLICANT
    }

    fun eligibilityFilled(project: OutputProject, newStatus: ProjectApplicationStatus): Boolean {
        val oldPossibilities = setOf(SUBMITTED, RESUBMITTED)
        val newPossibilities = setOf(ELIGIBLE, INELIGIBLE)

        return oldPossibilities.contains(project.projectStatus.status)
            && newPossibilities.contains(newStatus)
            && project.eligibilityAssessment != null
    }

    fun canSetQualityAssessment(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val allowedStatuses = listOf(SUBMITTED, RESUBMITTED, ELIGIBLE)

        return project.qualityAssessment == null
            && (isProgrammeUser() || isAdmin())
            && allowedStatuses.contains(project.projectStatus.status)
    }

    fun canSetEligibilityAssessment(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val allowedStatuses = listOf(SUBMITTED, RESUBMITTED)

        return project.eligibilityAssessment == null
            && (isProgrammeUser() || isAdmin())
            && allowedStatuses.contains(project.projectStatus.status)
    }

    fun isOwner(project: OutputProject): Boolean {
        return project.applicant.id == securityService.currentUser?.user?.id
    }

    fun isAdmin(): Boolean {
        return securityService.currentUser?.hasRole(ADMINISTRATOR)!!
    }

    fun isProgrammeUser(): Boolean {
        return securityService.currentUser?.hasRole(PROGRAMME_USER)!!
    }

}
