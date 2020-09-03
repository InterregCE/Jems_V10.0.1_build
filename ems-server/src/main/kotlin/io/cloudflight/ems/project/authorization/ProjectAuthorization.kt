package io.cloudflight.ems.project.authorization

import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus.Companion.isNotSubmittedNow
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
