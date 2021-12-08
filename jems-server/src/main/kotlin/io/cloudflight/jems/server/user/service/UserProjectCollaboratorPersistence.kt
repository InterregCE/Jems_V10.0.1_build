package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.server.user.entity.CollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject

interface UserProjectCollaboratorPersistence {

    fun getProjectIdsForUser(userId: Long): Set<Long>

    fun getUserIdsForProject(projectId: Long): List<CollaboratorAssignedToProject>

    fun changeUsersAssignedToProject(
        projectId: Long,
        usersToPersist: Map<Long, CollaboratorLevel>,
    ): List<CollaboratorAssignedToProject>

}
