package io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborator

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.authorization.CanRetrieveCollaborators
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerUserCollaborators(
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val securityService: SecurityService,
    private val userAuthorization: UserAuthorization
) : GetPartnerUserCollaboratorsInteractor {

    @CanRetrieveCollaborators
    @Transactional
    @ExceptionWrapper(GetPartnerUserCollaboratorsException::class)
    override fun getPartnerCollaborators(projectId: Long): Set<PartnerCollaborator> {
        if (userAuthorization.hasManageProjectPrivilegesPermission(projectId)) {
            return partnerCollaboratorPersistence.findPartnerCollaboratorsByProjectId(projectId)
        }
        // find available partners for this user first, then all collaborators to it
        val availablePartners = partnerCollaboratorPersistence
            .findPartnersByUserAndProject(securityService.getUserIdOrThrow(), projectId)
        return partnerCollaboratorPersistence
            .findByProjectAndPartners(projectId, availablePartners.map { it.partnerId }.toSet())
    }

}
