package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportWorkPackageOutputUpdate(
    val id: Long,
    val currentReport: BigDecimal,
    val progress: Set<InputTranslation>,
)
