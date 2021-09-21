package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.common.entity.TranslationView

interface WorkPackageRow: TranslationView {
    val id: Long
    val number: Int?
    val name: String?
    val specificObjective: String?
    val objectiveAndAudience: String?
}