package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableInvestmentsForReport

import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary

interface GetAvailableInvestmentsForReportInteractor {

    fun getInvestments(partnerId: Long, reportId: Long): List<InvestmentSummary>
}
