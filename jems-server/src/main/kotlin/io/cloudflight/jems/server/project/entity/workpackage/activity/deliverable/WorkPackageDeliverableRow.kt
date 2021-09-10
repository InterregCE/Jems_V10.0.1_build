package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import io.cloudflight.jems.server.common.entity.TranslationView

interface WorkPackageDeliverableRow: TranslationView {
    val deliverableId: Long
    val deliverableNumber: Int
    val startPeriod: Int?
    val description: String?
}
