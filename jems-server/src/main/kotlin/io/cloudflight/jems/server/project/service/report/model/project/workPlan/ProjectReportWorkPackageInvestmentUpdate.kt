package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportWorkPackageInvestmentUpdate(
    val id: Long,
    val progress: Set<InputTranslation>,
)
