package io.cloudflight.jems.server.project.service.workpackage.activity.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class WorkPackageActivity(
    val id: Long = 0,
    val workPackageId: Long,
    val workPackageNumber: Int = 0,
    val activityNumber: Int = 0,
    val title: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> =  emptySet(),
    val startPeriod: Int? = null,
    val endPeriod: Int? = null,
    var deliverables: List<WorkPackageActivityDeliverable> = emptyList(),
    var partnerIds: Set<Long> = emptySet()
)
