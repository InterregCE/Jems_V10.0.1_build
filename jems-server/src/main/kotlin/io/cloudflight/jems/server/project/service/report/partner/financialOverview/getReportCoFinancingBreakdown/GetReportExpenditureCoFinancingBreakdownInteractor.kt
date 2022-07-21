package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown

interface GetReportExpenditureCoFinancingBreakdownInteractor {

    fun get(partnerId: Long, reportId: Long): ExpenditureCoFinancingBreakdown

}
