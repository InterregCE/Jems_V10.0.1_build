package io.cloudflight.jems.api.payments

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal

data class PaymentPartnerDTO(
    val id: Long,
    val projectId: Long,
    val partnerId: Long,
    val partnerType: ProjectPartnerRoleDTO,
    val partnerNumber: Int?,
    val partnerAbbreviation: String,
    val amountApproved: BigDecimal?
)
