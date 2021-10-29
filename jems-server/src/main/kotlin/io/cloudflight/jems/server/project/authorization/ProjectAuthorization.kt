package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
// we need ProjectFormRetrieve here because otherwise user without ProjectRetrieve will
// not be able to see other project details than form
@PreAuthorize("@projectAuthorization.hasPermission('ProjectRetrieve') || @projectAuthorization.hasPermission('ProjectFormRetrieve', #projectId) || @projectAuthorization.isUserOwnerOrThrow(#projectId)")
annotation class CanRetrieveProject

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectRetrieve') || @projectAuthorization.hasPermission('ProjectFormRetrieve', #projectId) || @projectAuthorization.isUserOwnerOrThrow(#projectId)")
annotation class CanRetrieveProjectVersion

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectRetrieve')")
annotation class CanRetrieveProjects

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectsWithOwnershipRetrieve')")
annotation class CanRetrieveProjectsWithOwnership

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectCreate')")
annotation class CanCreateProject

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectFormRetrieve', #projectId) || @projectAuthorization.isUserOwnerOrThrow(#projectId)")
annotation class CanRetrieveProjectForm

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
annotation class CanUpdateProjectForm

@Component
class ProjectAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
) : Authorization(securityService) {

    fun isUserOwnerOrThrow(projectId: Long): Boolean {
        val isOwner = isActiveUserIdEqualTo(userId = projectPersistence.getApplicantAndStatusById(projectId).applicantId)
        if (isOwner)
            return true
        throw ResourceNotFoundException("project") // should be same exception as if entity not found
    }

    fun canUpdateProject(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val canSeeProject = hasPermission(UserRolePermission.ProjectFormUpdate, projectId) || isActiveUserIdEqualTo(project.applicantId)
        if (canSeeProject)
            return project.projectStatus.canBeModified()
        throw ResourceNotFoundException("project") // should be same exception as if entity not found
    }

}
