package io.cloudflight.jems.server.project.service.report.model.project.identification.overview

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportOutputIndicatorOverview(
    val id: Long?,
    val identifier: String,
    val name: Set<InputTranslation> = emptySet(),
    val measurementUnit: Set<InputTranslation> = emptySet(),
    val resultIndicator: ProjectReportResultIndicatorOverview?,
    var targetValue: BigDecimal,
    var previouslyReported: BigDecimal,
    var currentReport: BigDecimal,
) {
    override fun equals(other: Any?): Boolean = this === other || other is ProjectReportOutputIndicatorOverview && id === other.id
    override fun hashCode(): Int = id?.hashCode() ?: 0
}
