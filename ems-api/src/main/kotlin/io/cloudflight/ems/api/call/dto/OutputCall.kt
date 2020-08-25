package io.cloudflight.ems.api.call.dto

import io.cloudflight.ems.api.programme.dto.OutputProgrammePriorityPolicySimple
import java.time.ZonedDateTime

data class OutputCall (
    val id: Long?,
    val name: String,
    val priorityPolicies: List<OutputProgrammePriorityPolicySimple>,
    val status: CallStatus,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val description: String? = null
)
