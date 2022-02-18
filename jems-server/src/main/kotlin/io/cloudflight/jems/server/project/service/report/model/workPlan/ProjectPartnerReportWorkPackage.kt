package io.cloudflight.jems.server.project.service.report.model.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerReportWorkPackage(
    val id: Long,
    val number: Int,
    val description: Set<InputTranslation>,
    val activities: List<ProjectPartnerReportWorkPackageActivity>,
    val outputs: List<ProjectPartnerReportWorkPackageOutput>,
)
