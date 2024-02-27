package io.cloudflight.jems.api.project.dto.report.project.projectClosure

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportProjectClosureDTO(
    val story: Set<InputTranslation>,
    val prizes: List<ProjectReportProjectClosurePrizeDTO>,
)
