package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerCertificateFundSplit

interface ProjectReportFinancialOverviewPersistence {

    fun getOverviewPerFund(projectReportId: Long): List<FinancingSourceBreakdownLine>

    fun storeOverviewPerFund(projectReportId: Long, toStore: List<FinancingSourceBreakdownLine>): Map<Long, List<PartnerCertificateFundSplit>>

    fun getFundsToPartnerCertificateSplit(projectReportId: Long): Map<Long, List<PartnerCertificateFundSplit>>
}
