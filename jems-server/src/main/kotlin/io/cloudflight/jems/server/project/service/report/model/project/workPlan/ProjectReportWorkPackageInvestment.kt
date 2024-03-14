package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectPeriod

data class ProjectReportWorkPackageInvestment(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val period: ProjectPeriod?,
    val nutsRegion3: String?,
    val previousProgress: Set<InputTranslation>,
    val progress: Set<InputTranslation>,
    val status: ProjectReportWorkPlanInvestmentStatus?,
    val previousStatus: ProjectReportWorkPlanInvestmentStatus?,

    var statusLabel: ProjectReportWorkPlanFlag? = null
) {
    fun hasProgressChanged() = progress != previousProgress

}
