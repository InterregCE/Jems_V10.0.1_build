package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class UpdateProjectReportWorkPackageDTO(
    val id: Long,

    val specificStatus: ProjectReportWorkPlanStatusDTO?,
    val specificExplanation: Set<InputTranslation>,

    val communicationStatus: ProjectReportWorkPlanStatusDTO?,
    val communicationExplanation: Set<InputTranslation>,

    val completed: Boolean,
    val description: Set<InputTranslation>,
    val activities: List<UpdateProjectReportWorkPackageActivityDTO>,
    val outputs: List<UpdateProjectReportWorkPackageOutputDTO>,
)
