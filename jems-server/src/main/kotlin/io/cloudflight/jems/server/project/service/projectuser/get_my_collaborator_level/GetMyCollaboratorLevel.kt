package io.cloudflight.jems.server.project.service.projectuser.get_my_collaborator_level

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyCollaboratorLevel(
    private val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val securityService: SecurityService
) : GetMyCollaboratorLevelInteractor {

    // no security needed
    @Transactional
    @ExceptionWrapper(GetMyCollaboratorLevelException::class)
    override fun getMyCollaboratorLevel(projectId: Long): ProjectCollaboratorLevel? {

        // for collaborator, use project specific settings
        val userId = securityService.getUserIdOrThrow()
        var projectLevel = userProjectCollaboratorPersistence.getLevelForProjectAndUser(
            projectId = projectId,
            userId = userId,
        )
        if (projectLevel == null
            && partnerCollaboratorPersistence.findUserIdsByProjectId(projectId = projectId).contains(userId)) {
            projectLevel = ProjectCollaboratorLevel.VIEW
        }
        return projectLevel
    }

}
