package io.cloudflight.jems.server.project.entity.workpackage.output

import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface WorkPackageOutputRow: TranslationView {
    val outputNumber: Int
    val programmeOutputIndicatorId: Long?
    val programmeOutputIndicatorIdentifier: String?
    val targetValue: BigDecimal?
    val periodNumber: Int?
    val title: String?
    val description: String?
}