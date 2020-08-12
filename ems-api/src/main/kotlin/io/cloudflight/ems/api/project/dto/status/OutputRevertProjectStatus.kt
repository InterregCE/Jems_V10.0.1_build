package io.cloudflight.ems.api.project.dto.status

data class OutputRevertProjectStatus(
    val from: OutputProjectStatus,
    val to: OutputProjectStatus
)
