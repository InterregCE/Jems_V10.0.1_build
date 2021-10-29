package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

interface AssignUserToProjectInteractor {
    fun updateUserAssignmentsOnProject(projectId: Long, userIdsToRemove: Set<Long>, userIdsToAssign: Set<Long>): Set<Long>
}
