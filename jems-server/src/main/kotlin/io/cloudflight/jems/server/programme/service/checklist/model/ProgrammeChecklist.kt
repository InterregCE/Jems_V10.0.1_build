package io.cloudflight.jems.server.programme.service.checklist.model

import java.math.BigDecimal
import java.time.ZonedDateTime

open class ProgrammeChecklist(
    open val id: Long?,
    open val type: ProgrammeChecklistType,
    open val name: String?,
    open val minScore: BigDecimal?,
    open val maxScore: BigDecimal?,
    open val allowsDecimalScore: Boolean?,
    open val lastModificationDate: ZonedDateTime?,
    open var locked: Boolean
)
