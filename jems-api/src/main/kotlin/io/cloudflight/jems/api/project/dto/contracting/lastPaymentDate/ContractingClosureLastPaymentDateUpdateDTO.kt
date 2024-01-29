package io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate

import java.time.LocalDate

data class ContractingClosureLastPaymentDateUpdateDTO(
    val partnerId: Long,
    val lastPaymentDate: LocalDate?,
)
