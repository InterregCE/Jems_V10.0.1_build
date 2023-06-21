package io.cloudflight.jems.server.project.service.report.model.project.workPlan.create

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus

data class ProjectReportWorkPackageCreate(
    val workPackageId: Long?,
    val number: Int,
    val deactivated: Boolean,

    val specificObjective: Set<InputTranslation>,
    val specificStatus: ProjectReportWorkPlanStatus?,
    val previousSpecificStatus: ProjectReportWorkPlanStatus?,
    val previousSpecificExplanation: Set<InputTranslation>,
    val specificExplanation: Set<InputTranslation>,

    val communicationObjective: Set<InputTranslation>,
    val communicationStatus: ProjectReportWorkPlanStatus?,
    val previousCommunicationStatus: ProjectReportWorkPlanStatus?,
    val previousCommunicationExplanation: Set<InputTranslation>,
    val communicationExplanation: Set<InputTranslation>,

    val previousDescription: Set<InputTranslation>,
    val description: Set<InputTranslation>,

    val completed: Boolean,
    val previousCompleted: Boolean,
    val activities: List<ProjectReportWorkPackageActivityCreate>,
    val outputs: List<ProjectReportWorkPackageOutputCreate>,
    val investments: List<ProjectReportWorkPackageInvestmentCreate>,
)
