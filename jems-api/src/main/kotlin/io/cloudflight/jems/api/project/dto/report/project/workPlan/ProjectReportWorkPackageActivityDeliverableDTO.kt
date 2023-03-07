package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import java.math.BigDecimal

data class ProjectReportWorkPackageActivityDeliverableDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val period: ProjectPeriodDTO?,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,

    val progress: Set<InputTranslation>,
    val attachment: ProjectReportFileMetadataDTO?,
)
