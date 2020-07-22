package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.ProjectService
import org.springframework.stereotype.Component

@Component
class ProjectAuthorization(
    override val securityService: SecurityService,
    val projectService: ProjectService
): Authorization(securityService) {

    fun canReadProject(id: Long): Boolean {
        val project = projectService.getById(id)
        if (isAdmin() || isOwner(project))
            return true

        val status = project.projectStatus.status
        if (isProgrammeUser())
            return status != DRAFT

        return false
    }

    fun canCreateProject(): Boolean {
        return isAdmin() || isApplicantUser()
    }


    fun isOwner(project: OutputProject): Boolean {
        return project.applicant.id == securityService.currentUser?.user?.id
    }

}
