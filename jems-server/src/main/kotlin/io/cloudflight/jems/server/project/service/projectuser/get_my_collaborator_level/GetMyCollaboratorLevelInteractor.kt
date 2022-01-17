package io.cloudflight.jems.server.project.service.projectuser.get_my_collaborator_level

import io.cloudflight.jems.server.project.entity.projectuser.CollaboratorLevel

interface GetMyCollaboratorLevelInteractor {
    fun getMyCollaboratorLevel(projectId: Long): CollaboratorLevel?
}
