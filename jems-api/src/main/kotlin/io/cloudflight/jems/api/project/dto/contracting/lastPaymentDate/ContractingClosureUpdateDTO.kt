package io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate

import java.time.LocalDate

data class ContractingClosureUpdateDTO(
    val closureDate: LocalDate?,
    val lastPaymentDates: List<ContractingClosureLastPaymentDateUpdateDTO>,
)
