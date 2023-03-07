package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO

data class ProjectReportWorkPackageActivityDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val startPeriod: ProjectPeriodDTO?,
    val endPeriod: ProjectPeriodDTO?,

    val status: ProjectReportWorkPlanStatusDTO?,
    val progress: Set<InputTranslation>,

    val attachment: ProjectReportFileMetadataDTO?,

    val deliverables: List<ProjectReportWorkPackageActivityDeliverableDTO>,
)
