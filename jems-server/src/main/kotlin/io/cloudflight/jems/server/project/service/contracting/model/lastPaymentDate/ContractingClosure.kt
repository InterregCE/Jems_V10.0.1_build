package io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate

import java.time.LocalDate

data class ContractingClosure(
    val closureDate: LocalDate?,
    val lastPaymentDates: List<ContractingClosureLastPaymentDate>,
)
