package io.cloudflight.jems.api.project.dto.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import java.time.LocalDate

data class ChecklistInstanceDTO(
    val id: Long? = null,
    val status: ChecklistInstanceStatusDTO,
    val type: ProgrammeChecklistTypeDTO,
    val name: String?,
    val creatorEmail: String?,
    val finishedDate: LocalDate?,
    val relatedToId: Long,
    val programmeChecklistId: Long?,
    val consolidated: Boolean = false,
    val visible: Boolean = false
)
