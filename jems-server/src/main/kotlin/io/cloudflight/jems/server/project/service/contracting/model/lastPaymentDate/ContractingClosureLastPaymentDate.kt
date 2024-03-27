package io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.time.LocalDate

data class ContractingClosureLastPaymentDate(
    val partnerId: Long,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRole,
    val partnerDisabled: Boolean,
    val lastPaymentDate: LocalDate?,
)
