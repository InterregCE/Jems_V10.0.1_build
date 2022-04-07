package io.cloudflight.jems.server.project.service.checklist.model

data class CreateChecklistInstanceModel(
    val relatedToId: Long,
    val programmeChecklistId: Long,
)
