package io.cloudflight.jems.api.project.dto

data class ProjectModificationCreateDTO(
    val actionInfo: ApplicationActionInfoDTO,
    val correctionIds: Set<Long> = emptySet(),
)
