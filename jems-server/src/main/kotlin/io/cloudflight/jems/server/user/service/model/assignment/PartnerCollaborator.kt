package io.cloudflight.jems.server.user.service.model.assignment

import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.UserStatus

data class PartnerCollaborator(
    val userId: Long,
    val partnerId: Long,
    val userEmail: String,
    val sendNotificationsToEmail: Boolean,
    val userStatus: UserStatus,
    val level: PartnerCollaboratorLevel,
    val gdpr: Boolean
)
