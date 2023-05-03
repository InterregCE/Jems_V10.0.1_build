package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class UpdateProjectReportWorkPackageInvestmentDTO(
    val id: Long,
    val progress: Set<InputTranslation>,
)
