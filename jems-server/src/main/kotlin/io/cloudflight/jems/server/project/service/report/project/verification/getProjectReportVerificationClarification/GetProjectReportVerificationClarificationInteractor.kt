package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationClarification

import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification

interface GetProjectReportVerificationClarificationInteractor {
    fun getClarifications(projectId: Long, reportId: Long): List<ProjectReportVerificationClarification>
}