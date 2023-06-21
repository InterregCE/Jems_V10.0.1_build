package io.cloudflight.jems.server.project.service.report.project.financialOverview

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryCurrentlyReported
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPreviouslyReported
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory

interface ProjectReportCertificateCostCategoryPersistence {

    fun getCostCategories(projectId: Long, reportId: Long): ReportCertificateCostCategory

    fun getCostCategoriesCumulative(reportIds: Set<Long>): CertificateCostCategoryPreviouslyReported

    fun getCostCategoriesPerPartner(projectId: Long, reportId: Long): List<PerPartnerCostCategoryBreakdownLine>

    fun updateCurrentlyReportedValues(
        projectId: Long,
        reportId: Long,
        currentlyReported: CertificateCostCategoryCurrentlyReported
    )
}
