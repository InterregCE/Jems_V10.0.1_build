package io.cloudflight.jems.server.project.service.report.model.project.identification.overview

import java.math.BigDecimal

interface ProjectReportLineOverview {

    companion object {
        fun emptyResultIndicator() = ProjectReportResultIndicatorOverview(
            id = null,
            identifier = null,
            name = emptySet(),
            measurementUnit = emptySet(),
            baselineIndicator = BigDecimal.ZERO,
            baselines = emptyList(),
            targetValue = BigDecimal.ZERO,
            currentReport = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
        )
        fun emptyOutputIndicator() = ProjectReportOutputIndicatorOverview(
            id = null,
            identifier = "",
            name = emptySet(),
            measurementUnit = emptySet(),
            resultIndicator = null,
            targetValue = BigDecimal.ZERO,
            currentReport = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
        )
    }

    val targetValue: BigDecimal
    val currentReport: BigDecimal
    val previouslyReported: BigDecimal

    fun retrieveOutputIndicator(): ProjectReportOutputIndicatorOverview
    fun retrieveResultIndicator(): ProjectReportResultIndicatorOverview
}
