package io.cloudflight.jems.server.project.service.workpackage.activity.model

data class WorkPackageActivityDeliverable(
    val deliverableNumber: Int = 0,
    val translatedValues: Set<WorkPackageActivityDeliverableTranslatedValue> = emptySet(),
    val period: Int? = null,
)
