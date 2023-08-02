package io.cloudflight.jems.server.payments.entity

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "accounting_years")
class AccountingYearEntity(
    @Id
    val id: Long,

    @field:NotNull
    val name: String,

    @field:NotNull
    val startDate: LocalDate,

    @field:NotNull
    val endDate: LocalDate
)

