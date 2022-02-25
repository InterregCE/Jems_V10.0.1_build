package io.cloudflight.jems.api.project.dto.report.partner.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class UpdateProjectPartnerReportWorkPackageActivityDTO(
    val id: Long,
    val progress: Set<InputTranslation>,
    val deliverables: List<UpdateProjectPartnerReportWorkPackageActivityDeliverableDTO> = emptyList(),
)
