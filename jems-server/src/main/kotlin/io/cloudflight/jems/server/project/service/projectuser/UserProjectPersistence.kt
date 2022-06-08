package io.cloudflight.jems.server.project.service.projectuser

interface UserProjectPersistence {

    fun getProjectIdsForUser(userId: Long): Set<Long>

    fun getUserIdsForProject(projectId: Long): Set<Long>

    fun changeUsersAssignedToProject(projectId: Long, userIdsToRemove: Set<Long>, userIdsToAssign: Set<Long>): Set<Long>

    fun unassignUserFromProjects(userId: Long)

}
