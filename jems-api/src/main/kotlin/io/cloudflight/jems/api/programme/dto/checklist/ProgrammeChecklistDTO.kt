package io.cloudflight.jems.api.programme.dto.checklist

import java.time.ZonedDateTime

open class ProgrammeChecklistDTO(
    val id: Long? = null,
    val type: ProgrammeChecklistTypeDTO = ProgrammeChecklistTypeDTO.ELIGIBILITY,
    val name: String?,
    val lastModificationDate: ZonedDateTime?
)
