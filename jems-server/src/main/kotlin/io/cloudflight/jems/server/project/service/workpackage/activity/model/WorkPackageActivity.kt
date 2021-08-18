package io.cloudflight.jems.server.project.service.workpackage.activity.model

data class WorkPackageActivity(
    val workPackageId: Long,
    val activityNumber: Int = 0,
    val translatedValues: Set<WorkPackageActivityTranslatedValue> = emptySet(),
    val startPeriod: Int? = null,
    val endPeriod: Int? = null,
    var deliverables: List<WorkPackageActivityDeliverable> = emptyList(),
    val partnerIds: Set<Long> = emptySet(),
)
