package io.cloudflight.jems.server.project.service.report.project.base.getProjectReport

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport

interface GetProjectReportInteractor {

    fun findById(projectId: Long, reportId: Long): ProjectReport

}
