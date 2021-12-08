package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanAssignUsersToProjects
import io.cloudflight.jems.server.user.service.model.assignment.UpdateProjectUser
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieveEditUserAssignments
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileApplicationRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCheckApplicationForm
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectAssessmentView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStatusDecisionRevert
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStatusReturnToApplicant
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStartStepTwo
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileAssessmentRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectModificationFileAssessmentRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectModificationView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectOpenModification
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignUserToProject(
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val projectPersistence: ProjectPersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val eventPublisher: ApplicationEventPublisher
) : AssignUserToProjectInteractor {

    companion object {
        val GLOBAL_PROJECT_RETRIEVE_PERMISSIONS = setOf(
            ProjectRetrieve,
            ProjectRetrieveEditUserAssignments,
        )
        val PROJECT_MONITOR_PERMISSIONS = setOf(
            ProjectFormRetrieve,
            ProjectFileApplicationRetrieve,
            ProjectCheckApplicationForm,
            ProjectAssessmentView,
            ProjectStatusDecisionRevert,
            ProjectStatusReturnToApplicant,
            ProjectStartStepTwo,
            ProjectFileAssessmentRetrieve,
            ProjectModificationView,
            ProjectOpenModification,
            ProjectModificationFileAssessmentRetrieve
        )
    }

    @CanAssignUsersToProjects
    @Transactional
    @ExceptionWrapper(AssignUserToProjectException::class)
    override fun updateUserAssignmentsOnProject(data: Set<UpdateProjectUser>) {
        val automaticallyAssignedUsers = getAutomaticallyAssignedUsers()
        val availableUsers = getAvailableUsersByIdsAndRoles(
            userIds = data.map { it.userIdsToAdd }.flatten().toSet(),
            userRoleIds = getAvailableRoleIds(),
        )

        data.filter { (availableUsers.keys intersect it.userIdsToAdd).isNotEmpty() || it.userIdsToRemove.isNotEmpty() }
            .forEach {
                val userIds = userProjectPersistence.changeUsersAssignedToProject(
                    projectId = it.projectId,
                    userIdsToRemove = it.userIdsToRemove,
                    userIdsToAssign = availableUsers.keys intersect it.userIdsToAdd,
                )
                eventPublisher.publishEvent(
                    AssignUserEvent(
                        project = projectPersistence.getProjectSummary(it.projectId),
                        users = automaticallyAssignedUsers.plus(userPersistence.findAllByIds(userIds)),
                    )
                )
            }
    }

    private fun getAutomaticallyAssignedUsers() =
        userPersistence.findAllWithRoleIdIn(
            roleIds = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                needsToHaveAtLeastOneFrom = GLOBAL_PROJECT_RETRIEVE_PERMISSIONS,
                needsNotToHaveAnyOf = emptySet(),
            )
        )

    private fun getAvailableRoleIds() =
        userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = PROJECT_MONITOR_PERMISSIONS,
            needsNotToHaveAnyOf = GLOBAL_PROJECT_RETRIEVE_PERMISSIONS,
        )

    private fun getAvailableUsersByIdsAndRoles(userIds: Set<Long>, userRoleIds: Set<Long>) =
        userPersistence.findAllByIds(userIds)
            .filter { userRoleIds.contains(it.userRole.id) }
            .associateBy { it.id }
}
