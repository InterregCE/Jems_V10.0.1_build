package io.cloudflight.jems.server.project.entity.workpackage.output

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface WorkPackageOutputRow: TranslationView {
    val workPackageId: Long
    val outputNumber: Int
    val programmeOutputIndicatorId: Long?
    val programmeOutputIndicatorIdentifier: String?
    val programmeOutputIndicatorLanguage: SystemLanguage?
    val programmeOutputIndicatorName: String?
    val programmeOutputIndicatorMeasurementUnit: String?
    val targetValue: BigDecimal?
    val periodNumber: Int?
    val title: String?
    val description: String?
}
