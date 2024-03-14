package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectPeriod

data class ProjectReportWorkPackageActivity(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val startPeriod: ProjectPeriod?,
    val endPeriod: ProjectPeriod?,

    val previousStatus: ProjectReportWorkPlanStatus?,
    val status: ProjectReportWorkPlanStatus?,
    val previousProgress: Set<InputTranslation>,
    val progress: Set<InputTranslation>,

    val attachment: JemsFileMetadata?,

    val deliverables: List<ProjectReportWorkPackageActivityDeliverable>,
    var activityStatusLabel: ProjectReportWorkPlanFlag? = null
) {

    fun hasProgressChanged() = progress != previousProgress

    fun haveDeliverablesChanged() = deliverables.any {
            it.currentReport.compareTo(it.previousCurrentReport) != 0 || it.previousProgress != it.progress
        }

}
