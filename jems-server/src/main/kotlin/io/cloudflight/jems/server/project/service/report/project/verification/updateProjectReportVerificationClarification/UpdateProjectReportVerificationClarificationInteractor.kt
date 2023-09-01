package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationClarification

import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification

interface UpdateProjectReportVerificationClarificationInteractor {

    fun updateClarifications(
        projectId: Long,
        reportId: Long,
        clarifications: List<ProjectReportVerificationClarification>
    ): List<ProjectReportVerificationClarification>
}