package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportResultIndicatorOverview

import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview

interface GetProjectReportResultIndicatorOverviewInteractor {

    fun getResultIndicatorOverview(
        projectId: Long,
        reportId: Long
    ): Map<ProjectReportResultIndicatorOverview, Map<ProjectReportOutputIndicatorOverview, List<ProjectReportOutputLineOverview>>>
}
