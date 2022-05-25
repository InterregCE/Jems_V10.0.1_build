package io.cloudflight.jems.api.project.dto.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import java.time.LocalDate

data class ChecklistInstanceSelectionDTO(
    val id: Long? = null,
    val consolidated: Boolean,
    val status: ChecklistInstanceStatusDTO,
    val type: ProgrammeChecklistTypeDTO,
    val name: String?,
    val finishedDate: LocalDate?,
    val relatedToId: Long,
    val programmeChecklistId: Long?,
    val visible: Boolean
)
