package io.cloudflight.ems.api.call.dto

import io.cloudflight.ems.api.programme.dto.OutputProgrammePriorityPolicy
import java.time.ZonedDateTime

data class OutputCall (
    val id: Long?,
    val name: String,
    val priorityPolicies: List<OutputProgrammePriorityPolicy>,
    val status: CallStatus,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val description: String? = null
)
