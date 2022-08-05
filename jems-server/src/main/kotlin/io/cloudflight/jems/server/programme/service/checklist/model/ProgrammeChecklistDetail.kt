package io.cloudflight.jems.server.programme.service.checklist.model

import java.math.BigDecimal
import java.time.ZonedDateTime

class ProgrammeChecklistDetail(
    id: Long?,
    type: ProgrammeChecklistType,
    name: String?,
    minScore: BigDecimal?,
    maxScore: BigDecimal?,
    allowsDecimalScore: Boolean? = false,
    lastModificationDate: ZonedDateTime?,
    locked: Boolean,
    val components: List<ProgrammeChecklistComponent>? = emptyList()
) : ProgrammeChecklist(id, type, name, minScore, maxScore, allowsDecimalScore, lastModificationDate, locked)
