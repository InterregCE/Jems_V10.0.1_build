package io.cloudflight.jems.server.project.service.report.model.project.identification.overview

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportOutputLineOverview(
    val number: Int,
    val workPackageNumber: Int,
    val name: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val deactivated: Boolean,
    val outputIndicator: ProjectReportOutputIndicatorOverview?,
    val targetValue: BigDecimal,
    val currentReport: BigDecimal,
    val previouslyReported: BigDecimal,
)
