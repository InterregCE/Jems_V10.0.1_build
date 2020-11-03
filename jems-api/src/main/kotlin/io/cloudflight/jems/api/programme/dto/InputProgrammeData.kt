package io.cloudflight.jems.api.programme.dto

import io.cloudflight.jems.api.common.validator.StartDateBeforeEndDate
import java.time.LocalDate
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@StartDateBeforeEndDate("programme.lastYear.before.firstYear")
data class InputProgrammeData(

    @field:Size(max = 15, message = "programme.cci.size.too.long")
    val cci: String?,

    @field:Size(max = 255, message = "programme.title.size.too.long")
    val title: String?,

    @field:Size(max = 255, message = "programme.version.size.too.long")
    val version: String?,

    @field:Min(1000, message = "programme.firstYear.invalid.year")
    @field:Max(9999, message = "programme.firstYear.invalid.year")
    val firstYear: Int?,

    @field:Min(1000, message = "programme.lastYear.invalid.year")
    @field:Max(9999, message = "programme.lastYear.invalid.year")
    val lastYear: Int?,

    val eligibleFrom: LocalDate?,

    val eligibleUntil: LocalDate?,

    @field:Size(max = 255, message = "programme.commissionDecisionNumber.size.too.long")
    val commissionDecisionNumber: String?,

    val commissionDecisionDate: LocalDate?,

    @field:Size(max = 255, message = "programme.programmeAmendingDecisionNumber.size.too.long")
    val programmeAmendingDecisionNumber: String?,

    val programmeAmendingDecisionDate: LocalDate?
)

