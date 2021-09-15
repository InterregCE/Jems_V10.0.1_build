package io.cloudflight.jems.server.project.entity.workpackage.activity

import io.cloudflight.jems.server.common.entity.TranslationView

interface WorkPackageActivityRow: TranslationView {
    val id: Long
    val workPackageId: Long
    val workPackageNumber: Int?
    val activityNumber: Int
    val startPeriod: Int?
    val endPeriod: Int?
    val title: String?
    val description: String?
}
