package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportWorkPackage(
    val id: Long,
    val number: Int,
    val deactivated: Boolean,

    val specificObjective: Set<InputTranslation>,
    val specificStatus: ProjectReportWorkPlanStatus?,
    val specificExplanation: Set<InputTranslation>,

    val communicationObjective: Set<InputTranslation>,
    val communicationStatus: ProjectReportWorkPlanStatus?,
    val communicationExplanation: Set<InputTranslation>,

    val completed: Boolean,
    val description: Set<InputTranslation>,
    val activities: List<ProjectReportWorkPackageActivity>,
    val outputs: List<ProjectReportWorkPackageOutput>,
)
