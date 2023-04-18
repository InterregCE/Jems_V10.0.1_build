package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCoFinancingBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownPerPartnerData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateInvestmentBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateLumpSumBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateUnitCostBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportIdentificationData
import io.cloudflight.jems.plugin.contract.models.report.project.partnerCertificates.PartnerReportCertificateData
import io.cloudflight.jems.plugin.contract.models.report.project.projectResults.ProjectReportResultPrincipleData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageData
import io.cloudflight.jems.plugin.contract.services.report.ProjectReportDataProvider
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReport.GetProjectReport
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdownInteractor.GetReportCertificateInvestmentCalculatorService
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.GetReportCertificateCoFinancingBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown.GetReportCertificateCostCategoryBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown.GetReportCertificateLumpSumBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown.GetReportCertificateUnitCostCalculatorService
import io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner.sumOf
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification.GetProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProjectReportDataProviderImpl(
    private val projectReportPersistence: ProjectReportPersistence,
    private val getProjectReport: GetProjectReport,
    private val getIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val getIdentificationService: GetProjectReportIdentification,
    private val reportWorkPlanPersistence: ProjectReportWorkPlanPersistence,
    private val projectReportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence,
    private val projectReportCertificatePersistence: ProjectReportCertificatePersistence,
    private val partnerPersistence: PartnerPersistence,
    private val getReportCertificateCoFinancingBreakdownCalculator: GetReportCertificateCoFinancingBreakdownCalculator,
    private val getReportCertificateLumpSumBreakdownCalculator: GetReportCertificateLumpSumBreakdownCalculator,
    private val getReportCertificateCostCategoryBreakdownCalculator: GetReportCertificateCostCategoryBreakdownCalculator,
    private val reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence,
    private val reportCertificateUnitCostCalculatorService: GetReportCertificateUnitCostCalculatorService,
    private val reportCertificateInvestmentCalculatorService: GetReportCertificateInvestmentCalculatorService,

    ) : ProjectReportDataProvider {

    override fun get(projectId: Long, reportId: Long): ProjectReportData =
        projectReportPersistence.getReportById(projectId, reportId = reportId).let { report ->
            val periods = getProjectReport.getProjectPeriods(report.projectId, report.linkedFormVersion)
            report.toServiceModel { periodNumber -> periods.first { it.number == periodNumber } }
        }.toDataModel()

    override fun getIdentification(projectId: Long, reportId: Long): ProjectReportIdentificationData =
        this.getIdentificationPersistence.getReportIdentification(projectId, reportId).apply {
            spendingProfiles = getIdentificationService.getProjectReportSpendingProfiles(projectId, reportId)
        }.toDataModel()

    override fun getWorkPlan(projectId: Long, reportId: Long): List<ProjectReportWorkPackageData> =
        this.reportWorkPlanPersistence.getReportWorkPlanById(projectId = projectId, reportId = reportId)
            .toWorkPlanDataModel()

    override fun getProjectResults(projectId: Long, reportId: Long): ProjectReportResultPrincipleData =
        this.projectReportResultPrinciplePersistence.getProjectResultPrinciples(
            projectId = projectId,
            reportId = reportId
        ).toDataModel()

    override fun getPartnerCertificates(projectId: Long, reportId: Long): List<PartnerReportCertificateData> {
        val partnerIds = partnerPersistence.findTop30ByProjectId(projectId).mapTo(HashSet()) { it.id }
        return projectReportCertificatePersistence.listCertificates(
            partnerIds,
            pageable = Pageable.unpaged()
        ).content.toCertificateDataModel()
    }

    override fun getCoFinancingOverview(projectId: Long, reportId: Long): CertificateCoFinancingBreakdownData =
        this.getReportCertificateCoFinancingBreakdownCalculator.get(projectId, reportId).toDataModel()

    override fun getCostCategoryOverview(projectId: Long, reportId: Long): CertificateCostCategoryBreakdownData =
        this.getReportCertificateCostCategoryBreakdownCalculator.getSubmittedOrCalculateCurrent(projectId, reportId)
            .toDataModel()

    override fun getCostCategoryOverviewPerPartner(
        projectId: Long,
        reportId: Long
    ): CertificateCostCategoryBreakdownPerPartnerData {
        val data = reportCertificateCostCategoryPersistence.getCostCategoriesPerPartner(projectId, reportId = reportId)

        return PerPartnerCostCategoryBreakdown(
            partners = data.sortedBy { it.partnerNumber },
            totalCurrent = data.sumOf { it.current },
            totalDeduction = data.sumOf { it.deduction },
        ).toDataModel()
    }

    override fun getInvestmentOverview(projectId: Long, reportId: Long): CertificateInvestmentBreakdownData =
        this.reportCertificateInvestmentCalculatorService.getSubmittedOrCalculateCurrent(projectId, reportId)
            .toDataModel()


    override fun getLumpSumOverview(projectId: Long, reportId: Long): CertificateLumpSumBreakdownData =
        this.getReportCertificateLumpSumBreakdownCalculator.getSubmittedOrCalculateCurrent(projectId, reportId)
            .toDataModel()


    override fun getUnitCostOverview(projectId: Long, reportId: Long): CertificateUnitCostBreakdownData =
        this.reportCertificateUnitCostCalculatorService.getSubmittedOrCalculateCurrent(projectId, reportId)
            .toDataModel()

}
