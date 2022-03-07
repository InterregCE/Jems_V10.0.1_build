package io.cloudflight.jems.server.project.service.report.model.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup

data class ProjectPartnerReportIdentificationTargetGroup(
    val type: ProjectTargetGroup,
    val sortNumber: Int,
    val specification: Set<InputTranslation>,
    val description: Set<InputTranslation>,
)
