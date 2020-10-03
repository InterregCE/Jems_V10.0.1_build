package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.programme.dto.OutputProgrammePriorityPolicySimple

data class OutputCallProgrammePriority (
    val code: String,
    val title: String,
    val programmePriorityPolicies: List<OutputProgrammePriorityPolicySimple>
)
