package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportWorkPackageOnlyUpdate(
    val specificStatus: ProjectReportWorkPlanStatus?,
    val specificExplanation: Set<InputTranslation>,

    val communicationStatus: ProjectReportWorkPlanStatus?,
    val communicationExplanation: Set<InputTranslation>,

    val completed: Boolean,
    val description: Set<InputTranslation>,
)
