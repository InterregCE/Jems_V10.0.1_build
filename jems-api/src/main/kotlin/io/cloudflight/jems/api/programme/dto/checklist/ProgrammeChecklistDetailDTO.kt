package io.cloudflight.jems.api.programme.dto.checklist

import java.math.BigDecimal
import java.time.ZonedDateTime

class ProgrammeChecklistDetailDTO(
    id: Long? = null,
    type: ProgrammeChecklistTypeDTO = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
    name: String?,
    minScore: BigDecimal?,
    maxScore: BigDecimal?,
    allowsDecimalScore: Boolean? = false,
    lastModificationDate: ZonedDateTime?,
    locked: Boolean? = false,
    val components: List<ProgrammeChecklistComponentDTO> = emptyList()
) : ProgrammeChecklistDTO(id, type, minScore, maxScore, allowsDecimalScore, name, lastModificationDate, locked)
