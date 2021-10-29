package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.authorization.CanAssignUsersToProjects
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieveEditUserAssignments
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignUserToProject(
    private val userPersistence: UserPersistence,
    private val userProjectPersistence: UserProjectPersistence,
) : AssignUserToProjectInteractor {

    companion object {
        private val DEFAULT_PERMISSIONS = setOf(
            ProjectRetrieve,
            ProjectRetrieveEditUserAssignments,
        )
    }

    @CanAssignUsersToProjects
    @Transactional
    @ExceptionWrapper(AssignUserToProjectException::class)
    override fun updateUserAssignmentsOnProject(
        projectId: Long,
        userIdsToRemove: Set<Long>,
        userIdsToAssign: Set<Long>,
    ): Set<Long> =
        userProjectPersistence.changeUsersAssignedToProject(
            projectId = projectId,
            userIdsToRemove = userIdsToRemove,
            userIdsToAssign = userIdsToAssign.filterUsersIdsToThoseWithoutHiddenPermission(),
        )

    private fun Set<Long>.filterUsersIdsToThoseWithoutHiddenPermission(): Set<Long> =
        this.filterTo(HashSet()) { (userPersistence.getById(it).userRole.permissions intersect DEFAULT_PERMISSIONS).isEmpty() }

}
