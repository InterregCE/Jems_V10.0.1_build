package io.cloudflight.jems.api.project.dto.report.partner.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class UpdateProjectPartnerReportWorkPackageDTO(
    val id: Long,
    val description: Set<InputTranslation>,
    val activities: List<UpdateProjectPartnerReportWorkPackageActivityDTO> = emptyList(),
    val outputs: List<UpdateProjectPartnerReportWorkPackageOutputDTO> = emptyList(),
)
