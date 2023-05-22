package io.cloudflight.jems.server.project.service.report.model.project.workPlan.create

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus

data class ProjectReportWorkPackageActivityCreate(
    val activityId: Long?,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,
    val startPeriodNumber: Int?,
    val endPeriodNumber: Int?,
    val previousStatus: ProjectReportWorkPlanStatus?,
    val status: ProjectReportWorkPlanStatus?,
    val previousProgress: Set<InputTranslation>,
    val progress: Set<InputTranslation>,
    val deliverables: List<ProjectReportWorkPackageActivityDeliverableCreate>,
)
