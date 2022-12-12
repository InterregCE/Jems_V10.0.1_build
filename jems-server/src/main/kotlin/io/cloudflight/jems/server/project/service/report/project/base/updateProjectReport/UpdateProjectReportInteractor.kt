package io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate

interface UpdateProjectReportInteractor {

    fun updateReport(projectId: Long, reportId: Long, data: ProjectReportUpdate): ProjectReport

}
