package io.cloudflight.jems.server.project.service.report.model.workPlan.create

import io.cloudflight.jems.api.project.dto.InputTranslation

data class CreateProjectPartnerReportWorkPackageOutput(
    val number: Int,
    val title: Set<InputTranslation>,
)
