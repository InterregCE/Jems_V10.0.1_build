package io.cloudflight.jems.api.notification.dto

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO

data class NotificationPartnerDTO(
    val partnerId: Long,
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerNumber: Int,
)
