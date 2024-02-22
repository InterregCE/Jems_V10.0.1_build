package io.cloudflight.jems.server.project.service.report.model.project.closure

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportProjectClosurePrize(
    val id: Long?,
    val prize: Set<InputTranslation>,
    val orderNum: Int,
)
