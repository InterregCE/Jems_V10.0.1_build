package io.cloudflight.jems.server.authentication.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import org.springframework.stereotype.Component

@Component
class Authorization(
    open val securityService: SecurityService
) {

    fun getUser(): User = securityService.currentUser?.user!!

    fun hasPermission(permission: UserRolePermission): Boolean =
        hasPermission(permission, projectId = null)

    fun hasPermission(permission: UserRolePermission, projectId: Long? = null): Boolean {
        if (projectId == null) {
            if (!permission.projectRelated)
                return hasAuthority(permission)
            else
                throw IllegalArgumentException("Permission is related to project, but project has not been provided.")
        }

        return hasPermissionForProject(permission, projectId)
    }

    fun hasPermissionForProject(permission: UserRolePermission, projectId: Long) =
        permission.projectRelated && hasAuthority(permission) &&
            (getUser().assignedProjects.contains(projectId) || hasAuthority(ProjectRetrieve))

    private fun hasAuthority(permission: UserRolePermission): Boolean =
        securityService.currentUser?.hasPermission(permission)!!

    protected fun isActiveUserIdEqualTo(userId: Long): Boolean =
        userId == securityService.getUserIdOrThrow()

}
