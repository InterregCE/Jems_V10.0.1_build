package io.cloudflight.jems.server.user.service.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreatorCollaboratorsRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreatorCollaboratorsUpdate
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectMonitorCollaboratorsRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectMonitorCollaboratorsUpdate
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
    private val partnerCollaboratorRepository: UserPartnerCollaboratorRepository,
    private val projectCollaboratorRepository: UserProjectCollaboratorRepository,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : Authorization(securityService) {

    fun isThisUser(userId: Long): Boolean =
        securityService.currentUser?.user?.id == userId

    fun hasViewProjectPrivilegesPermission(projectId: Long) = hasView(projectId)

    private fun hasView(projectId: Long): Boolean {
        // monitor users
        if (hasPermission(ProjectMonitorCollaboratorsRetrieve, projectId))
            return true

        // partner collaborators and project collaborators
        val partnerCollaborators = partnerCollaboratorRepository.findAllByProjectId(projectId).mapTo(HashSet()) { it.id.userId }
        val projectCollaborators = projectCollaboratorRepository.findAllByIdProjectId(projectId).mapTo(HashSet()) { it.id.userId }
        val collaborators = partnerCollaborators union projectCollaborators
        if (isActiveUserIdEqualToOneOf(collaborators) && hasPermission(ProjectCreatorCollaboratorsRetrieve))
            return true

        // controllers
        val partnerControllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId)
        if (isActiveUserIdEqualToOneOf(partnerControllers) && hasNonProjectAuthority(ProjectMonitorCollaboratorsRetrieve))
            return true

        return false
    }

    fun hasManageProjectPrivilegesPermission(projectId: Long) =
        (hasPermission(ProjectCreatorCollaboratorsUpdate) && isActiveUserIdEqualToOneOf(projectPersistence.getApplicantAndStatusById(projectId).getUserIdsWithManageLevel()))
            || hasPermission(ProjectMonitorCollaboratorsUpdate, projectId)

}
