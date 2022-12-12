package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport

interface CreateProjectReportInteractor {

    fun createReportFor(projectId: Long): ProjectReport

}
