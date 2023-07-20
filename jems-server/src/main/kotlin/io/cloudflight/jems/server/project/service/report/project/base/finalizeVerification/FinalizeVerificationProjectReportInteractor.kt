package io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

interface FinalizeVerificationProjectReportInteractor {
    fun finalizeVerification(projectId: Long, reportId: Long): ProjectReportStatus
}
