package io.cloudflight.jems.api.programme.dto.checklist

import java.math.BigDecimal
import java.time.ZonedDateTime

open class ProgrammeChecklistDTO(
    val id: Long? = null,
    val type: ProgrammeChecklistTypeDTO = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
    val minScore: BigDecimal?,
    val maxScore: BigDecimal?,
    val allowsDecimalScore: Boolean? = false,
    val name: String?,
    val lastModificationDate: ZonedDateTime?,
    val locked: Boolean? = false
)
