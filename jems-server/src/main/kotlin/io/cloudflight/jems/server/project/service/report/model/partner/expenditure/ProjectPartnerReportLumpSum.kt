package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectPartnerReportLumpSum(
    val id: Long,
    val lumpSumProgrammeId: Long,
    val fastTrack: Boolean,
    val period: Int?,
    val cost: BigDecimal,
    val name: Set<InputTranslation> = emptySet(),
)
