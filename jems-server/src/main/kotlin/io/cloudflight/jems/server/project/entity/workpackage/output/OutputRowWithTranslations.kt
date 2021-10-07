package io.cloudflight.jems.server.project.entity.workpackage.output

import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface OutputRowWithTranslations: TranslationView {
    val workPackageId: Long
    val workPackageNumber: Int
    val number: Int
    val title: String?
    val targetValue: BigDecimal
    val programmeOutputId: Long?
    val programmeResultId: Long?
}
