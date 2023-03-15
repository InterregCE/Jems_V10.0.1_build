package io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class CreateProjectPartnerReportWorkPackageOutput(
    val number: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,
    val programmeOutputIndicatorId: Long?,
    val periodNumber: Int?,
    val targetValue: BigDecimal,
    val previouslyReported: BigDecimal?,
)
