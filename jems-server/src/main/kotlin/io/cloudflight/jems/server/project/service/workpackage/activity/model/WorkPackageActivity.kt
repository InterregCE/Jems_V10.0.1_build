package io.cloudflight.jems.server.project.service.workpackage.activity.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class WorkPackageActivity(
    val workPackageId: Long,
    val activityNumber: Int = 0,
    val title: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> =  emptySet(),
    val startPeriod: Int? = null,
    val endPeriod: Int? = null,
    var deliverables: List<WorkPackageActivityDeliverable> = emptyList(),
    var partnerIds: Set<Long> = emptySet()
)
