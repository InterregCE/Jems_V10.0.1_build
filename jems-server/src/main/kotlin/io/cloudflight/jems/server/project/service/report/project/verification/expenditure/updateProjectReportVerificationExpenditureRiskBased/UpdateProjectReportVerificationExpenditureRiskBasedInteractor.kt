package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased

interface UpdateProjectReportVerificationExpenditureRiskBasedInteractor {
    fun updateExpenditureVerificationRiskBased(
        projectId: Long,
        projectReportId: Long,
        riskBasedData: ProjectReportVerificationRiskBased
    ): ProjectReportVerificationRiskBased
}
