package io.cloudflight.jems.api.project.dto.report.project.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO

data class ProjectReportIdentificationTargetGroupDTO(
    val type: ProjectTargetGroupDTO,
    val sortNumber: Int,
    val description: Set<InputTranslation>,
)
