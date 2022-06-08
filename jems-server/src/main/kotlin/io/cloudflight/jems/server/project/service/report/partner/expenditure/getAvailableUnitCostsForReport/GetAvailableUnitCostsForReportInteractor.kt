package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableUnitCostsForReport

import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportUnitCost

interface GetAvailableUnitCostsForReportInteractor {
    fun getUnitCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportUnitCost>
}
