package io.cloudflight.jems.server.project.service.report.model.project.closure

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportProjectClosure(
    val story: Set<InputTranslation>,
    val prizes: List<Set<InputTranslation>>,
)
