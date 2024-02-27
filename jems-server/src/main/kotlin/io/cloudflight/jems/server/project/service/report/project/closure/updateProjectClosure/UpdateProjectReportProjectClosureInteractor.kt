package io.cloudflight.jems.server.project.service.report.project.closure.updateProjectClosure

import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure

interface UpdateProjectReportProjectClosureInteractor {

    fun update(reportId: Long, projectClosure: ProjectReportProjectClosure): ProjectReportProjectClosure
}
