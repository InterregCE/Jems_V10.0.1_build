package io.cloudflight.jems.server.project.service.report.model.project.base.create

import java.math.BigDecimal

data class ProjectReportResultCreate(
    val resultNumber: Int,
    val deactivated: Boolean,
    val periodNumber: Int?,
    val programmeResultIndicatorId: Long?,
    val baseline: BigDecimal,
    val targetValue: BigDecimal,
    val previouslyReported: BigDecimal,
)
