package io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate

import java.time.LocalDate

data class ContractingClosureDTO(
    val closureDate: LocalDate?,
    val lastPaymentDates: List<ContractingClosureLastPaymentDateDTO>,
)
