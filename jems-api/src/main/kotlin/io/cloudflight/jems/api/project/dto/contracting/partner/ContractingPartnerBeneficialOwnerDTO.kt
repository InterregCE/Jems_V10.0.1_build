package io.cloudflight.jems.api.project.dto.contracting.partner

import java.time.LocalDate

data class ContractingPartnerBeneficialOwnerDTO(
    val id: Long = 0,
    val partnerId: Long,
    val firstName: String,
    val lastName: String,
    val birth: LocalDate?,
    val vatNumber: String,
)
