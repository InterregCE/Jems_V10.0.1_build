package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentUpdateDTO(
    val id: Long?,
    val projectId: Long,
    val partnerId: Long,

    val programmeFundId: Long? = null,
    val partnerContributionId: Long? = null,
    val partnerContributionSpfId: Long? = null,

    val amountAdvance: BigDecimal?,
    val dateOfPayment: LocalDate? = null,
    val comment: String?,

    val paymentAuthorized: Boolean? = null,
    val paymentConfirmed: Boolean? = null,
)
