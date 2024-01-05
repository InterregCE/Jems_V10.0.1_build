package io.cloudflight.jems.server.programme.service.checklist.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class ProgrammeChecklistDetail(
    override val id: Long?,
    override val type: ProgrammeChecklistType,
    override val name: String?,
    override val minScore: BigDecimal?,
    override val maxScore: BigDecimal?,
    override val allowsDecimalScore: Boolean? = false,
    override val lastModificationDate: ZonedDateTime?,
    override var locked: Boolean,
    val components: List<ProgrammeChecklistComponent>? = emptyList()
) : ProgrammeChecklist(id, type, name, minScore, maxScore, allowsDecimalScore, lastModificationDate, locked)
