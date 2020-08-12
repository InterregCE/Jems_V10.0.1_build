package io.cloudflight.ems.project.authorization

import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.call.authorization.CallAuthorization
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.project.service.ProjectService
import io.cloudflight.ems.security.service.authorization.Authorization
import org.springframework.stereotype.Component

@Component
class ProjectAuthorization(
    override val securityService: SecurityService,
    val projectService: ProjectService,
    val callAuthorization: CallAuthorization
): Authorization(securityService) {

    fun canReadProject(id: Long): Boolean {
        val project = projectService.getById(id)
        if (isAdmin() || isOwner(project))
            return true

        val status = project.projectStatus.status
        if (isProgrammeUser())
            if (status != DRAFT)
                return true
            else
                throw ResourceNotFoundException("project")

        if (!isOwner(project))
            throw ResourceNotFoundException("project")

        return false
    }

    fun canCreateProjectForCall(callId: Long): Boolean {
        return callAuthorization.canReadCallDetail(callId)
            && (isAdmin() || isApplicantUser())
    }

    fun canUpdateProject(projectId: Long): Boolean {
        if (isAdmin())
            return true

        if (isApplicantUser()) {
            if (isOwner(projectService.getById(projectId)))
                return true
            else
                throw ResourceNotFoundException("project")
        }

        return false
    }

}
