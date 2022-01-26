package io.cloudflight.jems.server.project.service.partner.assign_user_collaborator_to_partner

import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator

interface AssignUserCollaboratorToPartnerInteractor {

    fun updateUserAssignmentsOnPartner(
        projectId: Long,
        partnerId: Long,
        emailsWithLevel: Set<Pair<String, PartnerCollaboratorLevel>>
    ): Set<PartnerCollaborator>
}
