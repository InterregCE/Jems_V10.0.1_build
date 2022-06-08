package io.cloudflight.jems.api.project.dto.report.partner.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerReportWorkPackageDTO(
    val id: Long,
    val number: Int,
    val description: Set<InputTranslation>,
    val activities: List<ProjectPartnerReportWorkPackageActivityDTO>,
    val outputs: List<ProjectPartnerReportWorkPackageOutputDTO>,
)
