package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost

interface GetProjectPartnerReportExpenditureInteractor {

    fun getExpenditureCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost>

}
