package io.cloudflight.jems.server.user.service.userproject.get_my_collaborator_level

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.user.entity.CollaboratorLevel
import io.cloudflight.jems.server.user.service.UserProjectCollaboratorPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyCollaboratorLevel(
    private val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val securityService: SecurityService,
) : GetMyCollaboratorLevelInteractor {

    // no security needed
    @Transactional
    @ExceptionWrapper(GetMyCollaboratorLevelException::class)
    override fun getMyCollaboratorLevel(projectId: Long): CollaboratorLevel? =
        userProjectCollaboratorPersistence.getLevelForProjectAndUser(
            projectId = projectId,
            userId = securityService.getUserIdOrThrow(),
        )

}
