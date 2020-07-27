package io.cloudflight.ems.api.programme.dto

data class OutputProgrammePriority (
    val id: Long,
    val code: String,
    val title: String,
    val objective: ProgrammeObjective,
    val programmePriorityPolicies: List<OutputProgrammePriorityPolicy>
)
