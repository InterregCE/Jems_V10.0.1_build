package io.cloudflight.jems.server.project.service.checklist.model

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ChecklistInstanceDetail(
    val id: Long,
    val programmeChecklistId: Long,
    val type: ProgrammeChecklistType?,
    val name: String?,
    val creatorEmail: String?,
    val creatorId: Long?,
    val createdAt: ZonedDateTime?,
    val status: ChecklistInstanceStatus,
    val finishedDate: LocalDate?,
    var relatedToId: Long?,
    val consolidated: Boolean?,
    val visible: Boolean,
    val minScore: BigDecimal?,
    val maxScore: BigDecimal?,
    val allowsDecimalScore: Boolean? = false,
    val components: List<ChecklistComponentInstance>? = emptyList()
)
