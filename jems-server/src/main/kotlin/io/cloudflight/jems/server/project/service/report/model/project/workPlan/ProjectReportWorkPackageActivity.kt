package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

data class ProjectReportWorkPackageActivity(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val startPeriod: ProjectPeriod?,
    val endPeriod: ProjectPeriod?,

    val status: ProjectReportWorkPlanStatus?,
    val progress: Set<InputTranslation>,

    val attachment: JemsFileMetadata?,

    val deliverables: List<ProjectReportWorkPackageActivityDeliverable>,
)
