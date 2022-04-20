package io.cloudflight.jems.server.programme.service.checklist.model

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import java.time.LocalDate

class ChecklistInstanceDetail(
    val id: Long,
    val programmeChecklistId: Long,
    val type: ProgrammeChecklistType?,
    val name: String?,
    val status: ChecklistInstanceStatus,
    val finishedDate: LocalDate?,
    var relatedToId: Long?,
    val components: List<ChecklistComponentInstance>? = emptyList()
)
