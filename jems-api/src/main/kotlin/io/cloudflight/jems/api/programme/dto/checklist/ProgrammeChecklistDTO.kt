package io.cloudflight.jems.api.programme.dto.checklist

import java.time.ZonedDateTime

open class ProgrammeChecklistDTO(
    val id: Long? = null,
    val type: ProgrammeChecklistTypeDTO = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
    val name: String?,
    val lastModificationDate: ZonedDateTime?,
    val locked: Boolean? = false
)
