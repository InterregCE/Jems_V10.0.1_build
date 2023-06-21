package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown

interface GetReportCertificateCostCategoryBreakdownInteractor {

    fun get(projectId: Long, reportId: Long): CertificateCostCategoryBreakdown

}
