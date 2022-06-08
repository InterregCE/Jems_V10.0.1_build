package io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborator

import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator

interface GetPartnerUserCollaboratorsInteractor {
    fun getPartnerCollaborators(projectId: Long): Set<PartnerCollaborator>
}
