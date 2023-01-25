package io.cloudflight.jems.server.project.service.report.model.project.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup

data class ProjectReportIdentificationTargetGroup(
    val type: ProjectTargetGroup,
    val sortNumber: Int,
    val description: Set<InputTranslation>,
)
