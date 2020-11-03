package io.cloudflight.jems.api.programme.dto.priority

data class OutputProgrammePriority (
    val id: Long,
    val code: String,
    val title: String,
    val objective: ProgrammeObjective,
    val programmePriorityPolicies: List<OutputProgrammePriorityPolicySimple>
)
