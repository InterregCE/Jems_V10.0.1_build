package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO

data class ProjectReportWorkPackageActivityDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val startPeriod: ProjectPeriodDTO?,
    val endPeriod: ProjectPeriodDTO?,

    val previousStatus: ProjectReportWorkPlanStatusDTO?,
    val status: ProjectReportWorkPlanStatusDTO?,
    val previousProgress: Set<InputTranslation>,
    val progress: Set<InputTranslation>,

    val attachment: JemsFileMetadataDTO?,

    val deliverables: List<ProjectReportWorkPackageActivityDeliverableDTO>,

    val activityStatusLabel: ProjectReportWorkPlanFlagDTO? = null
)
