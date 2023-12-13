package io.cloudflight.jems.server.project.service.report.project.base.reOpenVerificationProjectReport

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

interface ReOpenVerificationProjectReportInteractor {

    fun reOpen(projectId: Long, reportId: Long): ProjectReportStatus
}
