package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import java.math.BigDecimal

data class ProjectReportWorkPackageActivityDeliverable(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,

    val period: ProjectPeriod?,
    val previouslyReported: BigDecimal,
    val previousCurrentReport: BigDecimal,
    val currentReport: BigDecimal,

    val progress: Set<InputTranslation>,
    val previousProgress: Set<InputTranslation>,
    val attachment: JemsFileMetadata?,
)
