package io.cloudflight.jems.server.project.service.report.project.base.reOpenProjectReport

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

interface ReOpenProjectReportInteractor {
    fun reOpen(projectId: Long, reportId: Long): ProjectReportStatus
}
