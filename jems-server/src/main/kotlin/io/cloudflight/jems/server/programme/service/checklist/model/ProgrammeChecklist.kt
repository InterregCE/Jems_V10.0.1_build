package io.cloudflight.jems.server.programme.service.checklist.model

import java.math.BigDecimal
import java.time.ZonedDateTime

open class ProgrammeChecklist(
    val id: Long?,
    val type: ProgrammeChecklistType = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
    val name: String?,
    val minScore: BigDecimal?,
    val maxScore: BigDecimal?,
    val allowsDecimalScore: Boolean?,
    val lastModificationDate: ZonedDateTime?,
    var locked: Boolean
)
