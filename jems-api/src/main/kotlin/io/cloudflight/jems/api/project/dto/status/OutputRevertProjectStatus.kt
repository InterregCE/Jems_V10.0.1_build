package io.cloudflight.jems.api.project.dto.status

data class OutputRevertProjectStatus(
    val from: ProjectStatusDTO,
    val to: ProjectStatusDTO
)
