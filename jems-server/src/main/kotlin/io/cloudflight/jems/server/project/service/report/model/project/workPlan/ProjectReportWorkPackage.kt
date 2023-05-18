package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportWorkPackage(
    val id: Long,
    val number: Int,
    val deactivated: Boolean,

    val specificObjective: Set<InputTranslation>,
    val previousSpecificStatus: ProjectReportWorkPlanStatus?,
    val specificStatus: ProjectReportWorkPlanStatus?,
    val previousSpecificExplanation: Set<InputTranslation>,
    val specificExplanation: Set<InputTranslation>,

    val communicationObjective: Set<InputTranslation>,
    val previousCommunicationStatus: ProjectReportWorkPlanStatus?,
    val communicationStatus: ProjectReportWorkPlanStatus?,
    val previousCommunicationExplanation: Set<InputTranslation>,
    val communicationExplanation: Set<InputTranslation>,

    val previousCompleted: Boolean,
    val completed: Boolean,
    val previousDescription: Set<InputTranslation>,
    val description: Set<InputTranslation>,
    val activities: List<ProjectReportWorkPackageActivity>,
    val outputs: List<ProjectReportWorkPackageOutput>,
    val investments: List<ProjectReportWorkPackageInvestment>,

    var specificStatusLabel: ProjectReportWorkPlanFlag? = null,
    var communicationStatusLabel: ProjectReportWorkPlanFlag? = null,
    var workPlanStatusLabel: ProjectReportWorkPlanFlag? = null,
)
