package io.cloudflight.jems.server.project.service.report.project.base.startVerificationProjectReport

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

interface StartVerificationProjectReportInteractor {
    fun startVerification(projectId: Long, reportId: Long): ProjectReportStatus
}