package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.programme.dto.OutputProgrammeFund
import io.cloudflight.jems.api.programme.dto.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.strategy.ProgrammeStrategy
import java.time.ZonedDateTime

data class OutputCall (
    val id: Long?,
    val name: String,
    val priorityPolicies: List<OutputProgrammePriorityPolicySimple>,
    val strategies: List<ProgrammeStrategy>,
    val funds: List<OutputProgrammeFund>,
    val status: CallStatus,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val description: String? = null,
    val lengthOfPeriod: Int?
)
