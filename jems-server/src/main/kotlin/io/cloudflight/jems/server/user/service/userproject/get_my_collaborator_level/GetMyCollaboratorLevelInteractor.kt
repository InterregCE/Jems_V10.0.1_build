package io.cloudflight.jems.server.user.service.userproject.get_my_collaborator_level

import io.cloudflight.jems.server.user.entity.CollaboratorLevel

interface GetMyCollaboratorLevelInteractor {
    fun getMyCollaboratorLevel(projectId: Long): CollaboratorLevel?
}
