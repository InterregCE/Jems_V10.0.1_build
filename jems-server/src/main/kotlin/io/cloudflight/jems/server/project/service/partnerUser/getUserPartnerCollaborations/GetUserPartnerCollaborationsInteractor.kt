package io.cloudflight.jems.server.project.service.partnerUser.getUserPartnerCollaborations

import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator


interface GetUserPartnerCollaborationsInteractor {
    fun getUserPartnerCollaborations(projectId: Long): Set<PartnerCollaborator>
}
