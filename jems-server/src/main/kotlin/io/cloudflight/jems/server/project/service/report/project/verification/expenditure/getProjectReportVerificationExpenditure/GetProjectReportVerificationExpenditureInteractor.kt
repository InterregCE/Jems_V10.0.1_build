package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditure

import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine

interface GetProjectReportVerificationExpenditureInteractor {

    fun getExpenditureVerification(reportId: Long): List<ProjectReportVerificationExpenditureLine>

}
