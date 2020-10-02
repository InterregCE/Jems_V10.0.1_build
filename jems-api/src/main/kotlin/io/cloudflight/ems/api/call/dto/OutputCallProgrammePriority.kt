package io.cloudflight.ems.api.call.dto

import io.cloudflight.ems.api.programme.dto.OutputProgrammePriorityPolicySimple

data class OutputCallProgrammePriority (
    val code: String,
    val title: String,
    val programmePriorityPolicies: List<OutputProgrammePriorityPolicySimple>
)
