package io.cloudflight.jems.api.project.dto.workpackage.activity

import io.cloudflight.jems.api.project.dto.InputTranslation

data class WorkPackageActivityDTO(
    val id: Long,
    val workPackageId: Long,
    val activityNumber: Int? = null,
    val title: Set<InputTranslation> = emptySet(),
    val startPeriod: Int? = null,
    val endPeriod: Int? = null,
    val description: Set<InputTranslation> = emptySet(),
    val deliverables: List<WorkPackageActivityDeliverableDTO> = emptyList(),
    val partnerIds: Set<Long> = emptySet()
)

data class WorkPackageActivityDeliverableDTO(
    val deliverableId: Long,
    val activityId: Long,
    val deliverableNumber: Int? = null,
    val description: Set<InputTranslation> = emptySet(),
    val title: Set<InputTranslation> = emptySet(),
    val period: Int? = null
)
