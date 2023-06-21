package io.cloudflight.jems.server.project.service.report.model.project.workPlan.create

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import java.math.BigDecimal

data class ProjectReportWorkPackageOutputCreate(
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val programmeOutputIndicatorId: Long?,
    val periodNumber: Int?,
    val targetValue: BigDecimal,
    val previouslyReported: BigDecimal?,
    val previousCurrentReport: BigDecimal,
    val currentReport: BigDecimal,
    val previousProgress: Set<InputTranslation>,
    val progress: Set<InputTranslation>,
)
