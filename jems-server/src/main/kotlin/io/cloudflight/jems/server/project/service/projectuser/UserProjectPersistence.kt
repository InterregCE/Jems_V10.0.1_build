package io.cloudflight.jems.server.project.service.projectuser

import io.cloudflight.jems.server.user.service.model.UserSummary

interface UserProjectPersistence {

    fun getProjectIdsForUser(userId: Long): Set<Long>

    fun getUsersForProject(projectId: Long): Set<UserSummary>

    fun changeUsersAssignedToProject(projectId: Long, userIdsToRemove: Set<Long>, userIdsToAssign: Set<Long>): Set<Long>

    fun unassignUserFromProjects(userId: Long)

}
