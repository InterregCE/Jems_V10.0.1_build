package io.cloudflight.jems.server.project.service.report.model.project.projectResults

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportLineOverview.Companion.emptyResultIndicator
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import java.math.BigDecimal

data class ProjectReportProjectResult(
    val resultNumber: Int,
    val deactivated: Boolean,
    val programmeResultIndicatorId: Long?,
    val programmeResultIndicatorIdentifier: String?,
    val programmeResultIndicatorName: Set<InputTranslation>,
    val baseline: BigDecimal,
    override val targetValue: BigDecimal,
    override val currentReport: BigDecimal,
    override val previouslyReported: BigDecimal,
    val periodDetail: ProjectPeriod?,
    val description: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val attachment: JemsFileMetadata?,
) : ProjectReportLineOverview {

    override fun retrieveOutputIndicator(): ProjectReportOutputIndicatorOverview = throw UnsupportedOperationException()

    override fun retrieveResultIndicator(): ProjectReportResultIndicatorOverview =
        if (programmeResultIndicatorId == null)
            emptyResultIndicator()
        else
            ProjectReportResultIndicatorOverview(
                id = programmeResultIndicatorId,
                identifier = programmeResultIndicatorIdentifier,
                name = programmeResultIndicatorName,
                measurementUnit = measurementUnit,
                baselineIndicator = baseline,
                baselines = emptyList(),
                targetValue = BigDecimal.ZERO,
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
            )

}
