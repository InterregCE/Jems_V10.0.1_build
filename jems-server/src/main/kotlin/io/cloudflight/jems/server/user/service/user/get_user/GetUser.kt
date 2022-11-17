package io.cloudflight.jems.server.user.service.user.get_user

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanAssignUsersToProjects
import io.cloudflight.jems.server.user.service.authorization.CanRetrieveUser
import io.cloudflight.jems.server.user.service.authorization.CanRetrieveUsers
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUser(
    private val persistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val institutionPersistence: ControllerInstitutionPersistence
) : GetUserInteractor {

    @CanRetrieveUsers
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetUserException::class)
    override fun getUsers(pageable: Pageable, searchRequest: UserSearchRequest?): Page<UserSummary> =
        persistence.findAll(pageable, searchRequest)

    @CanAssignUsersToProjects
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetUsersFilteredByPermissionsException::class)
    override fun getUsersWithProjectRetrievePermissions(): List<UserSummary> =
        persistence.findAllWithRoleIdIn(
            roleIds = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                needsToHaveAtLeastOneFrom = UserRolePermission.getGlobalProjectRetrievePermissions(),
                needsNotToHaveAnyOf = emptySet(),
            )
        )

    @CanAssignUsersToProjects
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetUsersFilteredByPermissionsException::class)
    override fun getMonitorUsers(): List<UserSummary>  {
        val controllerInstitutionUsersIds = institutionPersistence.getAllControllerInstitutionUsersIds()
        return persistence.findAllWithRoleIdIn(
            roleIds = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                needsToHaveAtLeastOneFrom = UserRolePermission.getProjectMonitorPermissions(),
                needsNotToHaveAnyOf = UserRolePermission.getGlobalProjectRetrievePermissions(),
            )
        ).filter { it.id !in controllerInstitutionUsersIds }
    }


    @CanRetrieveUser
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetUserException::class)
    override fun getUserById(userId: Long): User =
        persistence.getById(userId).getUser()

}
