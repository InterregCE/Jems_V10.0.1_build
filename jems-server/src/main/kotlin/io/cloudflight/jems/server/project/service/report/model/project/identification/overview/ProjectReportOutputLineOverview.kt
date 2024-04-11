package io.cloudflight.jems.server.project.service.report.model.project.identification.overview

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportLineOverview.Companion.emptyOutputIndicator
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportLineOverview.Companion.emptyResultIndicator
import java.math.BigDecimal

data class ProjectReportOutputLineOverview(
    val number: Int,
    val workPackageNumber: Int,
    val name: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val deactivated: Boolean,
    val outputIndicator: ProjectReportOutputIndicatorOverview?,

    override val targetValue: BigDecimal,
    override val currentReport: BigDecimal,
    override val previouslyReported: BigDecimal,
) : ProjectReportLineOverview {

    override fun retrieveOutputIndicator(): ProjectReportOutputIndicatorOverview =
        outputIndicator ?: emptyOutputIndicator()

    override fun retrieveResultIndicator(): ProjectReportResultIndicatorOverview =
        outputIndicator?.resultIndicator ?: emptyResultIndicator()

}
