package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import java.time.ZonedDateTime

data class CallChecklistDTO(
    val id: Long? = null,
    val type: ProgrammeChecklistTypeDTO,
    val name: String?,
    val lastModificationDate: ZonedDateTime?,
    val selected: Boolean
)
