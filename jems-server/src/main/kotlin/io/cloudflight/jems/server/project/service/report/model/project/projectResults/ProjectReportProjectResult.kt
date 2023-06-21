package io.cloudflight.jems.server.project.service.report.model.project.projectResults

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import java.math.BigDecimal

data class ProjectReportProjectResult(
    val resultNumber: Int,
    val deactivated: Boolean,
    val programmeResultIndicatorId: Long?,
    val programmeResultIndicatorIdentifier: String?,
    val programmeResultIndicatorName: Set<InputTranslation>,
    val baseline: BigDecimal,
    val targetValue: BigDecimal,
    val achievedInReportingPeriod: BigDecimal,
    val cumulativeValue: BigDecimal,
    val periodDetail: ProjectPeriod?,
    val description: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val attachment: JemsFileMetadata?,
)
