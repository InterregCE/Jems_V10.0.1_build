package io.cloudflight.jems.server.project.service.projectuser

import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject

interface UserProjectCollaboratorPersistence {

    fun getProjectIdsForUser(userId: Long): Set<Long>

    fun getUserIdsForProject(projectId: Long): List<CollaboratorAssignedToProject>

    fun getLevelForProjectAndUser(projectId: Long, userId: Long): ProjectCollaboratorLevel?

    fun changeUsersAssignedToProject(
        projectId: Long,
        usersToPersist: Map<Long, ProjectCollaboratorLevel>,
    ): List<CollaboratorAssignedToProject>

}
