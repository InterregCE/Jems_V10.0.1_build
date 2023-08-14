package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportParkedExpenditureIds

interface GetProjectPartnerControlReportParkedExpendituresInteractor {
    fun getParkedExpenditureIds(partnerId: Long, reportId: Long): List<Long>
}
