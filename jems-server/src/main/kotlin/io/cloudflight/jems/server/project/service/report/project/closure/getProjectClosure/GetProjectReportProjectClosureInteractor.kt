package io.cloudflight.jems.server.project.service.report.project.closure.getProjectClosure

import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure

interface GetProjectReportProjectClosureInteractor {

    fun get(projectId: Long, reportId: Long): ProjectReportProjectClosure
}
