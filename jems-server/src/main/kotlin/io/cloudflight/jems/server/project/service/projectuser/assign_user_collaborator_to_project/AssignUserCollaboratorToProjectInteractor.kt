package io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project

import io.cloudflight.jems.server.project.entity.projectuser.CollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject

interface AssignUserCollaboratorToProjectInteractor {
    fun updateUserAssignmentsOnProject(projectId: Long, emailsWithLevel: Set<Pair<String, CollaboratorLevel>>): List<CollaboratorAssignedToProject>
}
