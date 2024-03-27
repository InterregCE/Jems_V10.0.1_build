package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal

data class PaymentPartnerDTO(
    val id: Long,

    val partnerReportId: Long?,
    val partnerReportNumber: Int?,

    val partnerId: Long,
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerNumber: Int?,
    val partnerAbbreviation: String,
    val nameInOriginalLanguage: String,
    val nameInEnglish: String,
    val amountApproved: BigDecimal?,

    val installments: List<PaymentPartnerInstallmentDTO>
)
