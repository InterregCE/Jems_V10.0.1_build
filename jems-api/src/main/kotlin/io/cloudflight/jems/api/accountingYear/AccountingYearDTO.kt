package io.cloudflight.jems.api.accountingYear

import java.time.LocalDate

data class AccountingYearDTO(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)

