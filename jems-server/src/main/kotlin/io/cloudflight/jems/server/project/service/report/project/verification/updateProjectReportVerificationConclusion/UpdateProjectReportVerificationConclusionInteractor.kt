package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationConclusion

import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion

interface UpdateProjectReportVerificationConclusionInteractor {

    fun updateVerificationConclusion(
        projectId: Long,
        reportId: Long,
        conclusion: ProjectReportVerificationConclusion
    ): ProjectReportVerificationConclusion
}