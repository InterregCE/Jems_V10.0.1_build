package io.cloudflight.jems.api.project.dto.report.project.projectResults

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import java.math.BigDecimal

data class ProjectReportProjectResultDTO(
    val resultNumber: Int,
    val deactivated: Boolean,
    val programmeResultIndicatorId: Long?,
    val programmeResultIndicatorIdentifier: String?,
    val programmeResultIndicatorName: Set<InputTranslation>?,
    val baseline: BigDecimal?,
    val targetValue: BigDecimal?,
    val currentReport: BigDecimal?,
    val previouslyReported: BigDecimal?,
    val periodDetail: ProjectPeriodDTO?,
    val description: Set<InputTranslation>?,
    val measurementUnit: Set<InputTranslation>?,
    val attachment: JemsFileMetadataDTO?,
)

