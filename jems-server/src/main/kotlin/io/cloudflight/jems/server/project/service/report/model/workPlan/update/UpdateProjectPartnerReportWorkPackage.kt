package io.cloudflight.jems.server.project.service.report.model.workPlan.update

import io.cloudflight.jems.api.project.dto.InputTranslation

data class UpdateProjectPartnerReportWorkPackage(
    val id: Long,
    val description: Set<InputTranslation>,
    val activities: List<UpdateProjectPartnerReportWorkPackageActivity>,
    val outputs: List<UpdateProjectPartnerReportWorkPackageOutput>,
)
