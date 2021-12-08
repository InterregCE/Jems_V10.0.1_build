package io.cloudflight.jems.server.user.service.userproject.get_user_collaborators_assigned_to_projects

import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject

interface GetUserCollaboratorsAssignedToProjectsInteractor {
    fun getUserIdsForProject(projectId: Long): List<CollaboratorAssignedToProject>
}
