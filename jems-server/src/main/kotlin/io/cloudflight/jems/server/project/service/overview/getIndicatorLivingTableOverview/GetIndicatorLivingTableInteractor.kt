package io.cloudflight.jems.server.project.service.overview.getIndicatorLivingTableOverview

import io.cloudflight.jems.server.project.service.report.model.overview.ProjectReportOutputIndicatorsAndResultsLivingTable
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview

interface GetIndicatorLivingTableInteractor {

    fun getResultIndicatorLivingTable(
        projectId: Long,
    ): Map<ProjectReportResultIndicatorOverview, ProjectReportOutputIndicatorsAndResultsLivingTable>

}
