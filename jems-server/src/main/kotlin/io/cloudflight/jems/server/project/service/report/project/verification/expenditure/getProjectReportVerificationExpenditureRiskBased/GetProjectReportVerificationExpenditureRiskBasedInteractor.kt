package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased

interface GetProjectReportVerificationExpenditureRiskBasedInteractor {

    fun getExpenditureVerificationRiskBasedData(projectId: Long, reportId: Long): ProjectReportVerificationRiskBased

}
