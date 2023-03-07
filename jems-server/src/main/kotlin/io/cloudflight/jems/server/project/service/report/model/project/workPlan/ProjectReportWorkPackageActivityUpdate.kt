package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportWorkPackageActivityUpdate(
    val id: Long,
    val status: ProjectReportWorkPlanStatus?,
    val progress: Set<InputTranslation>,
    val deliverables: List<ProjectReportWorkPackageActivityDeliverableUpdate>,
)
