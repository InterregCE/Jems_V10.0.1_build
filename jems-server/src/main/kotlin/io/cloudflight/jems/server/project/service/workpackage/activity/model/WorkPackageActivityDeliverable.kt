package io.cloudflight.jems.server.project.service.workpackage.activity.model

data class WorkPackageActivityDeliverable(
    val translatedValues: Set<WorkPackageActivityDeliverableTranslatedValue> = emptySet(),
    val period: Int? = null,
)
