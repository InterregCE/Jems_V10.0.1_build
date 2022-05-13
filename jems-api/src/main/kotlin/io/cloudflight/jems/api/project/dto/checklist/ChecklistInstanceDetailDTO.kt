package io.cloudflight.jems.api.project.dto.checklist

import java.time.LocalDate

open class ChecklistInstanceDetailDTO(
    val id: Long? = null,
    val status: ChecklistInstanceStatusDTO,
    val finishedDate: LocalDate?,
    val name: String?,
    val creatorEmail: String?,
    val consolidated: Boolean?,
    val components: List<ChecklistComponentInstanceDTO> = emptyList()
) {
    init {
        components.sortedWith(compareBy { it.position })
    }
}
