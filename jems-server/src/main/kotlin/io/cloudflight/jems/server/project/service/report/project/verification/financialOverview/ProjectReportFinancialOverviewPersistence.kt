package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine

interface ProjectReportFinancialOverviewPersistence {

    fun getOverviewPerFund(projectReportId: Long): List<FinancingSourceBreakdownLine>

    fun storeOverviewPerFund(projectReportId: Long, toStore: List<FinancingSourceBreakdownLine>): List<FinancingSourceBreakdownLine>

}
