package io.cloudflight.jems.server.payments.service.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class PartnerPayment(
    val id: Long,
    val projectId: Long,
    val orderNr: Int,
    val programmeLumpSumId: Long,
    val programmeFundId: Long,
    val partnerId: Long,
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int?,
    val partnerAbbreviation: String,
    val amountApprovedPerPartner: BigDecimal?,
)
