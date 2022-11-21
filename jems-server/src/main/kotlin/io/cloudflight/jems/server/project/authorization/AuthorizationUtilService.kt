package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.stereotype.Component

@Component
class AuthorizationUtilService(
    val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
) {

    fun userIsPartnerCollaboratorForProject(userId: Long, projectId: Long): Boolean =
        getUserPartnerCollaborations(userId = userId, projectId = projectId).isNotEmpty()

    private fun getUserPartnerCollaborations(userId: Long, projectId: Long): Set<PartnerCollaborator> =
        partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = userId,
            projectId = projectId,
        )

}
