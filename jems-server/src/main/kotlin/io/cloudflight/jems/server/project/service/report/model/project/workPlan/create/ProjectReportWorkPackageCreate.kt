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

    val communicationObjective: Set<InputTranslation>,
    val communicationStatus: ProjectReportWorkPlanStatus?,

    val completed: Boolean,
    val activities: List<ProjectReportWorkPackageActivityCreate>,
    val outputs: List<ProjectReportWorkPackageOutputCreate>,
    val investments: List<ProjectReportWorkPackageInvestmentCreate>,
)
