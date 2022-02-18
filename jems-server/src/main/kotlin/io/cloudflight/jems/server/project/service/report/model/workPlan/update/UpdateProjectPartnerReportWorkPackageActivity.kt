package io.cloudflight.jems.server.project.service.report.model.workPlan.update

import io.cloudflight.jems.api.project.dto.InputTranslation

data class UpdateProjectPartnerReportWorkPackageActivity(
    val id: Long,
    val progress: Set<InputTranslation>,
    val deliverables: List<UpdateProjectPartnerReportWorkPackageActivityDeliverable>,
)
