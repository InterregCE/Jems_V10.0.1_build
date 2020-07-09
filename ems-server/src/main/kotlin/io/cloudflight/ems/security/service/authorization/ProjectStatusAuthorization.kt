package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
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

        if (oldStatus == ProjectApplicationStatus.DRAFT && newStatus == ProjectApplicationStatus.SUBMITTED)
            return isOwner(project) || isAdmin()

        if (oldStatus == ProjectApplicationStatus.SUBMITTED && newStatus == ProjectApplicationStatus.RETURNED_TO_APPLICANT)
            return isProgrammeUser() || isAdmin()

        if (oldStatus == ProjectApplicationStatus.RETURNED_TO_APPLICANT && newStatus == ProjectApplicationStatus.RESUBMITTED)
            return isOwner(project) || isAdmin()

        if (oldStatus == ProjectApplicationStatus.RESUBMITTED && newStatus == ProjectApplicationStatus.RETURNED_TO_APPLICANT)
            return isProgrammeUser() || isAdmin()

        return false
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
