package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportWorkPackageDTO(
    val id: Long,
    val number: Int,
    val deactivated: Boolean,

    val specificObjective: Set<InputTranslation>,
    val previousSpecificStatus: ProjectReportWorkPlanStatusDTO?,
    val specificStatus: ProjectReportWorkPlanStatusDTO?,
    val previousSpecificExplanation: Set<InputTranslation>,
    val specificExplanation: Set<InputTranslation>,

    val communicationObjective: Set<InputTranslation>,
    val previousCommunicationStatus: ProjectReportWorkPlanStatusDTO?,
    val communicationStatus: ProjectReportWorkPlanStatusDTO?,
    val previousCommunicationExplanation: Set<InputTranslation>,
    val communicationExplanation: Set<InputTranslation>,

    val previousCompleted: Boolean,
    val completed: Boolean,
    val description: Set<InputTranslation>,
    val previousDescription: Set<InputTranslation>,
    val activities: List<ProjectReportWorkPackageActivityDTO>,
    val outputs: List<ProjectReportWorkPackageOutputDTO>,
    val investments: List<ProjectReportWorkPackageInvestmentDTO>,

    val specificStatusLabel: ProjectReportWorkPlanFlagDTO?,
    val communicationStatusLabel: ProjectReportWorkPlanFlagDTO?,
    val workPlanStatusLabel: ProjectReportWorkPlanFlagDTO?
)
