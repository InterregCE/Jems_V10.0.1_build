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

        if (oldStatus == DRAFT && newStatus == SUBMITTED)
            return isOwner(project) || isAdmin()

        if (oldStatus == SUBMITTED && newStatus == RETURNED_TO_APPLICANT)
            return isProgrammeUser() || isAdmin()

        if (oldStatus == RETURNED_TO_APPLICANT && newStatus == RESUBMITTED)
            return isOwner(project) || isAdmin()

        if (oldStatus == RESUBMITTED && newStatus == RETURNED_TO_APPLICANT)
            return isProgrammeUser() || isAdmin()

        return false
    }

    fun canSetQualityAssessment(projectId: Long): Boolean {
        val project = projectService.getById(projectId)
        val allowedStatuses = listOf(SUBMITTED, RESUBMITTED) // TODO MP2-373 add also , ELIGIBLE)

        return project.qualityAssessment == null
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
