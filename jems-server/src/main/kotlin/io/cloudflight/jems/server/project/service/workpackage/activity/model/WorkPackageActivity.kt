package io.cloudflight.jems.server.project.service.workpackage.activity.model

data class WorkPackageActivity(
    val translatedValues: Set<WorkPackageActivityTranslatedValue> = emptySet(),
    val startPeriod: Int? = null,
    val endPeriod: Int? = null,
    val deliverables: List<WorkPackageActivityDeliverable> = emptyList(),
)
