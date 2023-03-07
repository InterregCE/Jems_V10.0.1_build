package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorSummaryDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import java.math.BigDecimal

data class ProjectReportWorkPackageOutputDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val outputIndicator: OutputIndicatorSummaryDTO?,
    val period: ProjectPeriodDTO?,
    val targetValue: BigDecimal,
    val currentReport: BigDecimal,
    val previouslyReported: BigDecimal,
    val progress: Set<InputTranslation>,
    val attachment: ProjectReportFileMetadataDTO?,
)
