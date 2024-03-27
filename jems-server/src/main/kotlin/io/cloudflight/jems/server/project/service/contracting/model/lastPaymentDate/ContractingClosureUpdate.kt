package io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate

import java.time.LocalDate

data class ContractingClosureUpdate(
    val closureDate: LocalDate?,
    val lastPaymentDates: List<ContractingClosureLastPaymentDateUpdate>,
)
