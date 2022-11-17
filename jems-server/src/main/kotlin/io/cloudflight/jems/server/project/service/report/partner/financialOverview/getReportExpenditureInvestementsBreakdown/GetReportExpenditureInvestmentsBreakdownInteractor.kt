package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown

interface GetReportExpenditureInvestmentsBreakdownInteractor {
    fun get(partnerId: Long, reportId: Long): ExpenditureInvestmentBreakdown
}
