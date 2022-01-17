package io.cloudflight.jems.server.project.entity.result

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface ProjectResultRow: TranslationView {
    val resultNumber: Int
    val programmeResultIndicatorId: Long?
    val programmeResultIndicatorIdentifier: String?
    val programmeResultIndicatorLanguage: SystemLanguage?
    val programmeResultIndicatorName: String?
    val programmeResultIndicatorMeasurementUnit: String?
    val baseline: BigDecimal
    val targetValue: BigDecimal?
    val periodNumber: Int?
    val description: String?
}
