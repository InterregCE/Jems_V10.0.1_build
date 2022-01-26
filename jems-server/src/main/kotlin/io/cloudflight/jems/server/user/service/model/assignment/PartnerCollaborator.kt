package io.cloudflight.jems.server.user.service.model.assignment

import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel

data class PartnerCollaborator(
    val userId: Long,
    val partnerId: Long,
    val userEmail: String,
    val level: PartnerCollaboratorLevel,
)
