package io.cloudflight.jems.api.project.dto.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectPartnerReportLumpSumDTO(
    val id: Long,
    val lumpSumProgrammeId: Long,
    val period: Int?,
    val cost: BigDecimal,
    val name: Set<InputTranslation> = emptySet(),
)
