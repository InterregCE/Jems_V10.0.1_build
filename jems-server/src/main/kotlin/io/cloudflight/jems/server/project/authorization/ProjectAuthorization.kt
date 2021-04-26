package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.Companion.isDraft
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.Companion.isNotSubmittedNow
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.authorization.CallAuthorization
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
annotation class CanUpdateProject

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
annotation class CanReadProject

@Component
class ProjectAuthorization(
    override val securityService: SecurityService,
    val projectService: ProjectService,
    val callAuthorization: CallAuthorization
) : Authorization(securityService) {

    fun canReadProject(id: Long): Boolean {
        val project = projectService.getApplicantAndStatusById(id)
        if (isAdmin() || isApplicantOwner(project.applicantId))
            return true

        val status = project.projectStatus
        if (isProgrammeUser())
            if (!isDraft(status))
                return true
            else
                throw ResourceNotFoundException("project")

        if (isApplicantNotOwner(project.applicantId))
            throw ResourceNotFoundException("project")

        return false
    }

    fun canCreateProjectForCall(callId: Long): Boolean {
        return callAuthorization.canReadCall(callId)
            && (isAdmin() || isApplicantUser())
    }

    fun canUpdateProject(projectId: Long): Boolean {
        val project = projectService.getApplicantAndStatusById(projectId)
        val status = project.projectStatus
        if (isAdmin() || isApplicantOwner(project.applicantId))
            return isNotSubmittedNow(status)

        if (isProgrammeUser())
            if (!isDraft(status))
                return false
            else
                throw ResourceNotFoundException("project")

        if (isApplicantNotOwner(project.applicantId))
            throw ResourceNotFoundException("project")

        return false
    }

}
