package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.stereotype.Component

@Component
class AuthorizationUtilService(
    val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
) {

    fun userIsProjectOwnerOrProjectCollaborator(userId: Long, applicantAndStatus: ProjectApplicantAndStatus): Boolean =
         applicantAndStatus.applicantId == userId || userId in applicantAndStatus.getUserIdsWithViewLevel()

    fun userIsProjectCollaboratorWithEditPrivilege(userId: Long, applicantAndStatus: ProjectApplicantAndStatus): Boolean =
        userId in applicantAndStatus.getUserIdsWithEditLevel()

    fun userIsPartnerCollaboratorForProject(userId: Long, projectId: Long): Boolean =
        getUserPartnerCollaborations(userId = userId, projectId = projectId).isNotEmpty()

    private fun getUserPartnerCollaborations(userId: Long, projectId: Long): Set<PartnerCollaborator> =
        partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = userId,
            projectId = projectId,
        )
}
