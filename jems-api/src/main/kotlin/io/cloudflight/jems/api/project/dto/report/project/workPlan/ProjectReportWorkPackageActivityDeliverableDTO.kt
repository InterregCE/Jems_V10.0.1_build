package io.cloudflight.jems.api.project.dto.report.project.workPlan

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import java.math.BigDecimal

data class ProjectReportWorkPackageActivityDeliverableDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val period: ProjectPeriodDTO?,
    val previouslyReported: BigDecimal,
    val previousCurrentReport: BigDecimal,
    val currentReport: BigDecimal,

    val previousProgress: Set<InputTranslation>,
    val progress: Set<InputTranslation>,
    val attachment: JemsFileMetadataDTO?,
)
