package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdown

interface GetReportCertificateUnitCostsBreakdownInteractor {
    fun get(projectId: Long, reportId: Long): CertificateUnitCostBreakdown
}
