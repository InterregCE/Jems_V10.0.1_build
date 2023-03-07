package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class UpdateProjectReportWorkPackageActivityDeliverableDTO(
    val id: Long,
    val currentReport: BigDecimal,
    val progress: Set<InputTranslation>,
)
