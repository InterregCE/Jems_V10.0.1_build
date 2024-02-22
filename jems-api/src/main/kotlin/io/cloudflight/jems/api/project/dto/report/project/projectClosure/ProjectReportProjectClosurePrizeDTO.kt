package io.cloudflight.jems.api.project.dto.report.project.projectClosure

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportProjectClosurePrizeDTO(
    val id: Long? = null,
    val prize: Set<InputTranslation>,
    val orderNum: Int,
)
