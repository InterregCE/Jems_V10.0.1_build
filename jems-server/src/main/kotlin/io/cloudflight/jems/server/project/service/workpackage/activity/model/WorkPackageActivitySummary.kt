package io.cloudflight.jems.server.project.service.workpackage.activity.model

data class WorkPackageActivitySummary(
    val activityId: Long = 0,
    val workPackageNumber: Int,
    val activityNumber: Int = 0
)
