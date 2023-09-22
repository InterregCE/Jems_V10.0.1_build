package io.cloudflight.jems.server.project.service.report.project.financialOverview

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryCurrentlyReported
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPrevious
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine

interface ProjectReportCertificateCostCategoryPersistence {

    fun getCostCategories(projectId: Long, reportId: Long): ReportCertificateCostCategory

    fun getCostCategoriesCumulative(submittedReportIds: Set<Long>, finalizedReportIds: Set<Long>): CertificateCostCategoryPrevious

    fun getCostCategoriesPerPartner(projectId: Long, reportId: Long): List<PerPartnerCostCategoryBreakdownLine>

    fun updateCurrentlyReportedValues(
        projectId: Long,
        reportId: Long,
        currentlyReported: CertificateCostCategoryCurrentlyReported
    )

    fun updateAfterVerification(
        projectId: Long,
        reportId: Long,
        currentVerified: BudgetCostsCalculationResultFull
    )
}
