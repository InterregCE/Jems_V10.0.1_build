package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdown

interface GetReportExpenditureLumpSumBreakdownInteractor {

    fun get(partnerId: Long, reportId: Long): ExpenditureLumpSumBreakdown

}
