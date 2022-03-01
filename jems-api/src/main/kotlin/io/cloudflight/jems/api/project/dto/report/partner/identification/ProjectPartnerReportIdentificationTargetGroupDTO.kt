package io.cloudflight.jems.api.project.dto.report.partner.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO

data class ProjectPartnerReportIdentificationTargetGroupDTO(
    val type: ProjectTargetGroupDTO,
    val sortNumber: Int,
    val specification: Set<InputTranslation>,
    val description: Set<InputTranslation>,
)
