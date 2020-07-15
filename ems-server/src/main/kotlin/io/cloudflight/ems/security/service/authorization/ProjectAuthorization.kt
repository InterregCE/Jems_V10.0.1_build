package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.RETURNED_TO_APPLICANT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.SUBMITTED
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.ProjectService
import org.springframework.stereotype.Component

@Component
class ProjectAuthorization(
    val securityService: SecurityService,
    val projectService: ProjectService
) {

    fun canReadProject(id: Long): Boolean {
        val project = projectService.getById(id)
        if (isAdmin() || isOwner(project))
            return true

        val status = project.projectStatus.status
        if (isProgrammeUser())
            return status == SUBMITTED || status == RETURNED_TO_APPLICANT

        return false
    }

    fun canWriteProject(id: Long): Boolean {
        if (isAdmin())
            return true

        val project = projectService.getById(id)
        val status = project.projectStatus.status
        if (isOwner(project))
            return status == DRAFT || status == RETURNED_TO_APPLICANT

        return false
    }

    fun canCreateProject(): Boolean {
        return isAdmin() || isApplicantUser()
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

    fun isApplicantUser(): Boolean {
        return securityService.currentUser?.hasRole(APPLICANT_USER)!!
    }

}
