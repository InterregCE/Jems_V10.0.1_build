package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class UpdateProjectReportWorkPackageActivityDTO(
    val id: Long,
    val status: ProjectReportWorkPlanStatusDTO?,
    val progress: Set<InputTranslation>,
    val deliverables: List<UpdateProjectReportWorkPackageActivityDeliverableDTO>,
)
