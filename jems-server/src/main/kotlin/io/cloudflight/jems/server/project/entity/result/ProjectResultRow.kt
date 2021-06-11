package io.cloudflight.jems.server.project.entity.result

import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface ProjectResultRow: TranslationView {
    val resultNumber: Int
    val programmeResultIndicatorId: Long?
    val programmeResultIndicatorIdentifier: String?
    val targetValue: BigDecimal?
    val periodNumber: Int?
    val description: String?
}
