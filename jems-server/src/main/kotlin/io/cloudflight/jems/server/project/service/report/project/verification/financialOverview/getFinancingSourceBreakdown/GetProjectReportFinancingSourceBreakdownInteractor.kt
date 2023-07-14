package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown

interface GetProjectReportFinancingSourceBreakdownInteractor {

    fun get(projectId: Long, reportId: Long): FinancingSourceBreakdown

}
