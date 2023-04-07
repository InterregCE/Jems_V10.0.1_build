package io.cloudflight.jems.api.project.dto.checklist

import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

open class ChecklistInstanceDetailDTO(
    val id: Long? = null,
    val status: ChecklistInstanceStatusDTO,
    val finishedDate: LocalDate?,
    val name: String?,
    val creatorEmail: String?,
    val consolidated: Boolean?,
    val createdAt: ZonedDateTime?,
    val minScore: BigDecimal?,
    val maxScore: BigDecimal?,
    val allowsDecimalScore: Boolean? = false,
    val components: List<ChecklistComponentInstanceDTO> = emptyList()
) {
    init {
        components.sortedWith(compareBy { it.position })
    }
}
