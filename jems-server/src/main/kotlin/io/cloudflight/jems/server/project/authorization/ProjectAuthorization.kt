package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus.Companion.isNotSubmittedNow
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus.DRAFT
import io.cloudflight.jems.server.call.authorization.CallAuthorization
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.security.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.security.service.authorization.Authorization
import org.springframework.stereotype.Component

@Component
class ProjectAuthorization(
    override val securityService: SecurityService,
    val projectService: ProjectService,
    val callAuthorization: CallAuthorization
): Authorization(securityService) {

    fun canReadProject(id: Long): Boolean {
        val project = projectService.getApplicantAndStatusById(id)
        if (isAdmin() || isApplicantOwner(project.applicantId))
            return true

        val status = project.projectStatus
        if (isProgrammeUser())
            if (status != DRAFT)
                return true
            else
                throw ResourceNotFoundException("project")

        if (isApplicantNotOwner(project.applicantId))
            throw ResourceNotFoundException("project")

        return false
    }

    fun canCreateProjectForCall(callId: Long): Boolean {
        return callAuthorization.canReadCallDetail(callId)
            && (isAdmin() || isApplicantUser())
    }

    fun canUpdateProject(projectId: Long): Boolean {
        val project = projectService.getApplicantAndStatusById(projectId)
        val status = project.projectStatus
        if (isAdmin() || isApplicantOwner(project.applicantId))
            return isNotSubmittedNow(status)

        if (isProgrammeUser())
            if (status != DRAFT)
                return false
            else
                throw ResourceNotFoundException("project")

        if (isApplicantNotOwner(project.applicantId))
            throw ResourceNotFoundException("project")

        return false
    }

}
