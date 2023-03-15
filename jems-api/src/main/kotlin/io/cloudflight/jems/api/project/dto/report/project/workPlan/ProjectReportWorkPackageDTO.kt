package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportWorkPackageDTO(
    val id: Long,
    val number: Int,
    val deactivated: Boolean,

    val specificObjective: Set<InputTranslation>,
    val specificStatus: ProjectReportWorkPlanStatusDTO?,
    val specificExplanation: Set<InputTranslation>,

    val communicationObjective: Set<InputTranslation>,
    val communicationStatus: ProjectReportWorkPlanStatusDTO?,
    val communicationExplanation: Set<InputTranslation>,

    val completed: Boolean,
    val description: Set<InputTranslation>,
    val activities: List<ProjectReportWorkPackageActivityDTO>,
    val outputs: List<ProjectReportWorkPackageOutputDTO>,
)
