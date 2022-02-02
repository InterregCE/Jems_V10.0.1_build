package io.cloudflight.jems.server.programme.service.model

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import java.time.LocalDate

data class ProgrammeData(
    val cci: String?,
    val title: String?,
    val version: String?,
    val firstYear: Int?,
    val lastYear: Int?,
    val eligibleFrom: LocalDate?,
    val eligibleUntil: LocalDate?,
    val commissionDecisionNumber: String?,
    val commissionDecisionDate: LocalDate?,
    val programmeAmendingDecisionNumber: String?,
    val programmeAmendingDecisionDate: LocalDate?,
    val projectIdProgrammeAbbreviation: String?,
    val projectIdUseCallId: Boolean,
    val programmeNuts: List<OutputNuts>
)
