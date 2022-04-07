package io.cloudflight.jems.api.programme.dto.checklist

import java.time.ZonedDateTime

class ProgrammeChecklistDetailDTO(
    id: Long? = null,
    type: ProgrammeChecklistTypeDTO = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
    name: String?,
    lastModificationDate: ZonedDateTime?,
    val components: List<ProgrammeChecklistComponentDTO> = emptyList()
) : ProgrammeChecklistDTO(id, type, name, lastModificationDate)
