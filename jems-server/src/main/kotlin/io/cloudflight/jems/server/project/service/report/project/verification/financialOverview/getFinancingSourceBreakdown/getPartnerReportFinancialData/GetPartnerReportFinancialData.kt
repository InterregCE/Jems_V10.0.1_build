package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.getPartnerReportFinancialData

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerReportFinancialData
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.extractOverview
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerReportFinancialData(
    private val projectPartnerReportPersistence: ProjectPartnerReportPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportPartnerPersistence: ProjectPartnerReportPersistence,
    private val reportContributionPersistence: ProjectPartnerReportContributionPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
) {

    @Transactional(readOnly = true)
    fun retrievePartnerReportFinancialData(reportId: Long): PartnerReportFinancialData {
        val partnerId = projectPartnerReportPersistence.getPartnerReportByIdUnsecured(reportId).partnerId

        return PartnerReportFinancialData(
            coFinancingFromAF = reportPartnerPersistence
                .getPartnerReportById(partnerId = partnerId, reportId = reportId).identification.coFinancing,
            contributionsFromAF = reportContributionPersistence
                .getPartnerReportContribution(partnerId = partnerId, reportId = reportId).extractOverview(),
            totalEligibleBudgetFromAF = reportExpenditureCoFinancingPersistence
                .getCoFinancing(partnerId = partnerId, reportId = reportId).totalsFromAF.sum,
            flatRatesFromAF = reportExpenditureCostCategoryPersistence
                .getCostCategories(partnerId = partnerId, reportId = reportId).options,
        )
    }
}
