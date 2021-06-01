package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.authorization.CallAuthorization
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieve') || @projectAuthorization.isUserOwnerOfProject(#projectId)")
annotation class CanRetrieveProject

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieve')")
annotation class CanRetrieveProjects

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectAuthorization.canOwnerUpdateProject(#projectId)")
annotation class CanUpdateProject

@Component
class ProjectAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
    val callAuthorization: CallAuthorization
) : Authorization(securityService) {

    fun isUserOwnerOfProject(projectId: Long): Boolean {
        val isOwner = isActiveUserIdEqualTo(userId = projectPersistence.getApplicantAndStatusById(projectId).applicantId)
        if (isOwner)
            return true
        throw ResourceNotFoundException("project") // should be same exception as if entity not found
    }

    fun canReadProject(id: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(id)
        if (isAdmin() || isApplicantOwner(project.applicantId) || isProgrammeUser())
            return true

        if (isApplicantNotOwner(project.applicantId))
            throw ResourceNotFoundException("project")

        return false
    }

    fun canCreateProjectForCall(callId: Long): Boolean {
        return callAuthorization.canReadCall(callId)
            && (isAdmin() || isApplicantUser())
    }

    fun canOwnerUpdateProject(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val isOwner = isActiveUserIdEqualTo(project.applicantId)
        if (isOwner)
            return project.projectStatus.isNotSubmittedNow()
        throw ResourceNotFoundException("project") // should be same exception as if entity not found
    }

}
