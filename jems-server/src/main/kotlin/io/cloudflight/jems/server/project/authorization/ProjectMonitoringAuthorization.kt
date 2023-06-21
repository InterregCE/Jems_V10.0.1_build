package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectMonitoringAuthorization.canViewProjectMonitoring(#projectId)")
annotation class CanViewProjectMonitoring

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectSetToContracted', #projectId)")
annotation class CanEditProjectMonitoring

@Component
class ProjectMonitoringAuthorization(
    override val securityService: SecurityService,
    val authorizationUtilService: AuthorizationUtilService
): Authorization(securityService) {

    fun canViewProjectMonitoring(projectId: Long): Boolean {
        return hasPermissionForProject(UserRolePermission.ProjectContractingView, projectId)
            || hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId)
            || authorizationUtilService.hasPermissionAsController(UserRolePermission.ProjectContractingView, projectId)
    }

    fun canEditProjectMonitoring(projectId: Long): Boolean =
        hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId)

}
