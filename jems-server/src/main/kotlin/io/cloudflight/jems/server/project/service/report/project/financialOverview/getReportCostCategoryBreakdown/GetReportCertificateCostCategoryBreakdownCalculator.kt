package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportCertificateCostCategoryBreakdownCalculator(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val certificateCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence,
) {

    /**
     * This fun will either
     *  - just fetch reported certificate cost category breakdown for submitted report
     *  - or calculate currently reported values from actual certificate cost category
     */
    @Transactional(readOnly = true)
    fun getSubmittedOrCalculateCurrent(projectId: Long, reportId: Long): CertificateCostCategoryBreakdown {
        val reportStatus = reportPersistence.getReportById(projectId = projectId, reportId).status
        val data = reportCertificateCostCategoryPersistence.getCostCategories(projectId = projectId, reportId = reportId)

        val costCategories = data.toLinesModel()

        if (reportStatus.isOpenForNumbersChanges()) {
            val certificateIds = reportCertificatePersistence.listCertificatesOfProjectReport(reportId)
                .mapTo(HashSet()) { it.id }

            val certificateCurrentValues = certificateCostCategoryPersistence.getCostCategoriesTotalEligible(certificateIds)
            val spfContributionCurrentValues = reportSpfClaimPersistence.getCurrentSpfContribution(reportId)
            val currentValues = certificateCurrentValues.plusSpf(spfContributionCurrentValues.sum)

            costCategories.fillInCurrent(current = currentValues)
        }

        return costCategories.fillInOverviewFields()
    }

}
