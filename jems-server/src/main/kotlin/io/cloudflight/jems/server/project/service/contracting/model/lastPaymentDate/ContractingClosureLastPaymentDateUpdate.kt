package io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate

import java.time.LocalDate

data class ContractingClosureLastPaymentDateUpdate(
    val partnerId: Long,
    val lastPaymentDate: LocalDate,
)
