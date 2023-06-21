package io.cloudflight.jems.server.notification.inApp.service.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class NotificationPartner(
    val partnerId: Long,
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int,
)
