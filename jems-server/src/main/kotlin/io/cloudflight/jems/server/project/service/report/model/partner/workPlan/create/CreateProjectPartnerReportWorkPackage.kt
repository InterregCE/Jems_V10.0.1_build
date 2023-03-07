package io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create

import io.cloudflight.jems.api.project.dto.InputTranslation

data class CreateProjectPartnerReportWorkPackage(
    val workPackageId: Long?,
    val number: Int,
    val deactivated: Boolean,

    val specificObjective: Set<InputTranslation>,
    val communicationObjective: Set<InputTranslation>,

    val activities: List<CreateProjectPartnerReportWorkPackageActivity>,
    val outputs: List<CreateProjectPartnerReportWorkPackageOutput>,
)
