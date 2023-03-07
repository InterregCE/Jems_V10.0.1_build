package io.cloudflight.jems.api.project.dto.report.project.projectResults

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

class UpdateProjectReportProjectResultDTO(
    val resultNumber: Int,
    val achievedInReportingPeriod: BigDecimal?,
    val description: Set<InputTranslation> = mutableSetOf(),
)
