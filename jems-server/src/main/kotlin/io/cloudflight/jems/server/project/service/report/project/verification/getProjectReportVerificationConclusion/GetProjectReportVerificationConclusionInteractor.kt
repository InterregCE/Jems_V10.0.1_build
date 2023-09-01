package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationConclusion

import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion

interface GetProjectReportVerificationConclusionInteractor {

    fun getVerificationConclusion(projectId: Long, reportId: Long): ProjectReportVerificationConclusion
}