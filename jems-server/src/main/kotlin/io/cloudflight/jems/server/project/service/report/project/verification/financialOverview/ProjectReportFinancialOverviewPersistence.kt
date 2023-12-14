package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerCertificateFundSplit

interface ProjectReportFinancialOverviewPersistence {

    fun getOverviewPerFund(projectReportId: Long): List<FinancingSourceBreakdownLine>
    fun getOverviewSpfPerFund(projectReportId: Long): FinancingSourceBreakdownLine?

    fun storeOverviewPerFund(
        projectReportId: Long,
        toStore: List<FinancingSourceBreakdownLine>,
        spfPartnerIdInCaseOfSpf: Long?,
    ): List<PartnerCertificateFundSplit>
}
