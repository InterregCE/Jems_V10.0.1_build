package io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.time.LocalDate

data class ContractingClosureLastPaymentDateDTO(
    val partnerId: Long,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerDisabled: Boolean,
    val lastPaymentDate: LocalDate?,
)
