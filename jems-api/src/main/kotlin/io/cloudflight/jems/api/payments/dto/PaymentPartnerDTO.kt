package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal

data class PaymentPartnerDTO(
    val id: Long,
    val partnerId: Long,
    val partnerType: ProjectPartnerRoleDTO,
    val partnerNumber: Int?,
    val partnerAbbreviation: String,
    val amountApproved: BigDecimal?,

    val partnerReportId: Long?,
    val partnerReportNumber: Int?,

    val installments: List<PaymentPartnerInstallmentDTO>
)
