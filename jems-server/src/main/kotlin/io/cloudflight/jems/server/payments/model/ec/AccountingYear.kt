package io.cloudflight.jems.server.payments.model.ec

import java.time.LocalDate

data class AccountingYear(
    val id: Long,
    val year: Short,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
