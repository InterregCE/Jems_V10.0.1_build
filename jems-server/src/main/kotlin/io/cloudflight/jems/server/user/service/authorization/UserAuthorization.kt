package io.cloudflight.jems.server.user.service.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCollaboratorsRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCollaboratorsUpdate
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('UserRetrieve')")
annotation class CanRetrieveUsers

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('UserRetrieve') || @userAuthorization.isThisUser(#userId)")
annotation class CanRetrieveUser

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('UserCreate')")
annotation class CanCreateUser

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('UserUpdate')")
annotation class CanUpdateUser

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('UserUpdatePassword')")
annotation class CanUpdateUserPassword

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieveEditUserAssignments')")
annotation class CanAssignUsersToProjects

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@userAuthorization.hasViewProjectPrivilegesPermission(#projectId)")
annotation class CanRetrieveCollaborators

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@userAuthorization.hasManageProjectPrivilegesPermission(#projectId)")
annotation class CanUpdateCollaborators

@Component
class UserAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
) : Authorization(securityService) {

    fun isThisUser(userId: Long): Boolean =
        securityService.currentUser?.user?.id == userId

    fun hasViewProjectPrivilegesPermission(projectId: Long) =
        (hasPermission(ProjectCreate) && hasAuthority(ProjectCollaboratorsRetrieve) && isActiveUserIdEqualToOneOf(projectPersistence.getApplicantAndStatusById(projectId).getUserIdsWithViewLevel()))
            ||
            (hasPermissionForProject(ProjectCollaboratorsRetrieve, projectId))

    fun hasManageProjectPrivilegesPermission(projectId: Long) =
        hasPermission(ProjectCreate) && hasAuthority(ProjectCollaboratorsUpdate) && isActiveUserIdEqualToOneOf(projectPersistence.getApplicantAndStatusById(projectId).getUserIdsWithManageLevel())

}
