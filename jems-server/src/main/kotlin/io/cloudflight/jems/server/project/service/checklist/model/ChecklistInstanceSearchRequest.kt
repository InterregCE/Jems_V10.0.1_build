package io.cloudflight.jems.server.project.service.checklist.model

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType

data class ChecklistInstanceSearchRequest(
    val relatedToId: Long? = null,
    val type: ProgrammeChecklistType? = null,
    val creatorId: Long? = null,
    val status: ChecklistInstanceStatus? = null,
    val visible: Boolean? = null,
)
