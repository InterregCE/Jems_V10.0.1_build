package io.cloudflight.jems.server.programme.service.checklist.model

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import java.time.LocalDate

data class ChecklistInstance(
    val id: Long? = null,
    val status: ChecklistInstanceStatus,
    val type: ProgrammeChecklistType,
    val name: String?,
    val creatorEmail: String?,
    val relatedToId: Long?,
    val finishedDate: LocalDate? = null,
    val programmeChecklistId: Long?,
    val consolidated: Boolean = false
)
