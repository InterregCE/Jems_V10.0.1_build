package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.project.dto.InputTranslation

data class OutputCallProgrammePriority (
    val code: String,
    val title: Set<InputTranslation>,
    val programmePriorityPolicies: List<OutputProgrammePriorityPolicySimple>
)
