package io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview

import io.cloudflight.jems.server.project.service.result.model.IndicatorOverviewLine

interface GetProjectResultIndicatorsOverviewInteractor {
    fun getProjectResultIndicatorOverview(projectId: Long, version: String? = null): List<IndicatorOverviewLine>
}
