package io.cloudflight.jems.server.user.service.userproject.get_user_collaborators_assigned_to_projects

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.user.service.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.authorization.CanRetrieveCollaborators
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserCollaboratorsAssignedToProjects(
    private val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
) : GetUserCollaboratorsAssignedToProjectsInteractor {

    @CanRetrieveCollaborators
    @Transactional
    @ExceptionWrapper(GetUserCollaboratorsAssignedToProjectsException::class)
    override fun getUserIdsForProject(projectId: Long): List<CollaboratorAssignedToProject> =
        userProjectCollaboratorPersistence.getUserIdsForProject(projectId)

}
