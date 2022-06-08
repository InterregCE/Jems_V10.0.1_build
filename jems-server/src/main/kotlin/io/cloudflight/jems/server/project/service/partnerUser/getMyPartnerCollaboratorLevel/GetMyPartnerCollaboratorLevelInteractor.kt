package io.cloudflight.jems.server.project.service.partnerUser.getMyPartnerCollaboratorLevel

import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel


interface GetMyPartnerCollaboratorLevelInteractor {
    fun getMyPartnerCollaboratorLevel(partnerId: Long): PartnerCollaboratorLevel?
}
