package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdown

interface GetReportExpenditureUnitCostBreakdownInteractor {

    fun get(partnerId: Long, reportId: Long): ExpenditureUnitCostBreakdown

}
