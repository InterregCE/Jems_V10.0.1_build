package io.cloudflight.jems.api.project.dto.checklist

data class CreateChecklistInstanceDTO(
    val relatedToId: Long,
    val programmeChecklistId: Long,
)
