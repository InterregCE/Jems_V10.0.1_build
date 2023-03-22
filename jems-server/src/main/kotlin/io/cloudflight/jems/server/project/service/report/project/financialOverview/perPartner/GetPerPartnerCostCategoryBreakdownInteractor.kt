package io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown

interface GetPerPartnerCostCategoryBreakdownInteractor {

    fun get(projectId: Long, reportId: Long): PerPartnerCostCategoryBreakdown

}
