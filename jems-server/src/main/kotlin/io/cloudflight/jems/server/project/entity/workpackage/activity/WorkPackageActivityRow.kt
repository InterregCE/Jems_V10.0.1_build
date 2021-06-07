package io.cloudflight.jems.server.project.entity.workpackage.activity

import io.cloudflight.jems.server.common.entity.TranslationView

interface WorkPackageActivityRow: TranslationView {
    val activityNumber: Int
    val startPeriod: Int?
    val endPeriod: Int?
    val title: String?
    val description: String?
}