package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectSharedFolderAuthorization.canRetrieveSharedFolderFile(#projectId)")
annotation class CanRetrieveSharedFolder

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectSharedFolderAuthorization.canEditSharedFolderFile(#projectId)")
annotation class CanEditSharedFolder

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectSharedFolderAuthorization.canDeleteSharedFolderFile(#projectId)")
annotation class CanDeleteSharedFolderFile

@Component
class ProjectSharedFolderAuthorization(
    override val securityService: SecurityService,
    private val projectPersistence: ProjectPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : Authorization(securityService) {

    fun canRetrieveSharedFolderFile(projectId: Long): Boolean =
        hasSharedFolderPermission(SharedFolderPermission.View, projectId)

    fun canEditSharedFolderFile(projectId: Long): Boolean =
        hasSharedFolderPermission(SharedFolderPermission.Edit, projectId)

    fun canDeleteSharedFolderFile(projectId: Long): Boolean =
        hasPermission(SharedFolderPermission.Edit.monitor, projectId)


    private fun hasSharedFolderPermission(
        needed: SharedFolderPermission,
        projectId: Long,
    ): Boolean {
        // monitor users
        if (hasPermission(needed.monitor, projectId))
            return true

        // partner collaborators and project collaborators
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val projectUserIds = when (needed) {
            SharedFolderPermission.View -> project.getUserIdsWithViewLevel()
            SharedFolderPermission.Edit -> project.getUserIdsWithEditLevel()
        }
        if (isActiveUserIdEqualToOneOf(projectUserIds) && hasNonProjectAuthority(needed.creator))
            return true

        // controllers
        val partnerControllerUserIds = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId)
        return isActiveUserIdEqualToOneOf(partnerControllerUserIds) && hasNonProjectAuthority(needed.monitor)
    }

    private enum class SharedFolderPermission(val monitor: UserRolePermission, val creator: UserRolePermission) {
        View(UserRolePermission.ProjectMonitorSharedFolderView, UserRolePermission.ProjectCreatorSharedFolderView),
        Edit(UserRolePermission.ProjectMonitorSharedFolderEdit, UserRolePermission.ProjectCreatorSharedFolderEdit),
    }

}
