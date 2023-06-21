package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO

data class ProjectReportWorkPackageInvestmentDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val period: ProjectPeriodDTO?,
    val nutsRegion3: String?,
    val previousProgress: Set<InputTranslation>,
    val progress: Set<InputTranslation>,
)
