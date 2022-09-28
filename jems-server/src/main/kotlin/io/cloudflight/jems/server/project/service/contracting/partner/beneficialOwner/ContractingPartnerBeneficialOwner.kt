package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner

import java.time.LocalDate

data class ContractingPartnerBeneficialOwner(
    val id: Long,
    val partnerId: Long,
    val firstName: String,
    val lastName: String,
    val birth: LocalDate?,
    val vatNumber: String,
)
