package io.cloudflight.jems.api.project.dto.workpackage.activity

import io.cloudflight.jems.api.project.dto.InputTranslation

data class WorkPackageActivityDTO(
    val workPackageId: Long,
    val activityNumber: Int? = null,
    val title: Set<InputTranslation> = emptySet(),
    val startPeriod: Int? = null,
    val endPeriod: Int? = null,
    val description: Set<InputTranslation> = emptySet(),
    val deliverables: List<WorkPackageActivityDeliverableDTO> = emptyList()
)

data class WorkPackageActivityDeliverableDTO(
    val deliverableNumber: Int? = null,
    val description: Set<InputTranslation> = emptySet(),
    val period: Int? = null
)
