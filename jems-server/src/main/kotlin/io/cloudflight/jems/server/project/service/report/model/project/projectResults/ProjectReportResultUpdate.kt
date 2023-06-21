package io.cloudflight.jems.server.project.service.report.model.project.projectResults

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportResultUpdate(
    val currentValue: BigDecimal,
    val description: Set<InputTranslation>,
)
