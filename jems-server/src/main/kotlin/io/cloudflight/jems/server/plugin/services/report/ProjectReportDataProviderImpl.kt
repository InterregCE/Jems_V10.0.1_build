package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.report.project.closure.ProjectReportClosureData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCoFinancingBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownPerPartnerData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateInvestmentBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateLumpSumBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateUnitCostBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportBaseData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportIdentificationData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportIdentificationExtendedData
import io.cloudflight.jems.plugin.contract.models.report.project.partnerCertificates.PartnerReportCertificateData
import io.cloudflight.jems.plugin.contract.models.report.project.projectResults.ProjectReportResultPrincipleData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageData
import io.cloudflight.jems.plugin.contract.services.report.ProjectReportDataProvider
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.closurePeriod
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdown.GetReportCertificateInvestmentCalculatorService
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.GetReportCertificateCoFinancingBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown.GetReportCertificateCostCategoryBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown.GetReportCertificateLumpSumBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown.GetReportCertificateUnitCostCalculatorService
import io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner.sumOf
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification.ProjectReportSpendingProfileCalculator
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectReportDataProviderImpl(
    private val projectReportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val getIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val projectReportSpendingProfileCalculator: ProjectReportSpendingProfileCalculator,
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
    private val projectReportProjectClosurePersistence: ProjectReportProjectClosurePersistence
): ProjectReportDataProvider {

    @Transactional(readOnly = true)
    override fun get(projectId: Long, reportId: Long): ProjectReportData =
        projectReportPersistence.getReportById(projectId, reportId = reportId).let { report ->
            val periods = projectPersistence.getProjectPeriods(report.projectId, report.linkedFormVersion).plus(closurePeriod)
            report.toServiceModel({ periodNumber -> periods.first { it.number == periodNumber } })
        }.toDataModel()

    @Transactional(readOnly = true)
    override fun getAllProjectReportsBaseDataByProjectId(projectId: Long): Sequence<ProjectReportBaseData> =
        projectReportPersistence.getAllProjectReportsBaseDataByProjectId(projectId)

    @Transactional(readOnly = true)
    override fun getIdentification(projectId: Long, reportId: Long): ProjectReportIdentificationData =
        this.getIdentificationPersistence.getReportIdentification(projectId, reportId).apply {
            spendingProfilePerPartner = projectReportSpendingProfileCalculator.getProjectReportSpendingProfiles(projectId, reportId)
        }.toDataModel()


    @Transactional(readOnly = true)
    override fun getIdentificationData(projectId: Long, reportId: Long): ProjectReportIdentificationExtendedData =
        this.getIdentificationPersistence.getReportIdentification(projectId, reportId).apply {
            spendingProfilePerPartner = projectReportSpendingProfileCalculator.getProjectReportSpendingProfiles(projectId, reportId)
        }.toIdentificationDataModel()

    @Transactional(readOnly = true)
    override fun getWorkPlan(projectId: Long, reportId: Long): List<ProjectReportWorkPackageData> =
        this.reportWorkPlanPersistence.getReportWorkPlanById(projectId = projectId, reportId = reportId)
            .toWorkPlanDataModel()

    @Transactional(readOnly = true)
    override fun getProjectResults(projectId: Long, reportId: Long): ProjectReportResultPrincipleData =
        this.projectReportResultPrinciplePersistence.getProjectResultPrinciples(
            projectId = projectId,
            reportId = reportId
        ).toDataModel()

    @Transactional(readOnly = true)
    override fun getPartnerCertificates(projectId: Long, reportId: Long): List<PartnerReportCertificateData> {
        val partnerIds = partnerPersistence.findTop50ByProjectId(projectId).mapTo(HashSet()) { it.id }
        return projectReportCertificatePersistence.listCertificates(
            partnerIds,
            pageable = Pageable.unpaged()
        ).content.toCertificateDataModel()
    }

    @Transactional(readOnly = true)
    override fun getCoFinancingOverview(projectId: Long, reportId: Long): CertificateCoFinancingBreakdownData =
        this.getReportCertificateCoFinancingBreakdownCalculator.get(projectId, reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getCostCategoryOverview(projectId: Long, reportId: Long): CertificateCostCategoryBreakdownData =
        this.getReportCertificateCostCategoryBreakdownCalculator.getSubmittedOrCalculateCurrent(projectId, reportId)
            .toDataModel()

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    override fun getInvestmentOverview(projectId: Long, reportId: Long): CertificateInvestmentBreakdownData =
        this.reportCertificateInvestmentCalculatorService.getSubmittedOrCalculateCurrent(projectId, reportId)
            .toDataModel()

    @Transactional(readOnly = true)
    override fun getLumpSumOverview(projectId: Long, reportId: Long): CertificateLumpSumBreakdownData =
        this.getReportCertificateLumpSumBreakdownCalculator.getSubmittedOrCalculateCurrent(projectId, reportId)
            .toDataModel()

    @Transactional(readOnly = true)
    override fun getUnitCostOverview(projectId: Long, reportId: Long): CertificateUnitCostBreakdownData =
        this.reportCertificateUnitCostCalculatorService.getSubmittedOrCalculateCurrent(projectId, reportId)
            .toDataModel()

    @Transactional(readOnly = true)
    override fun getClosureData(projectId: Long, reportId: Long): ProjectReportClosureData =
        this.projectReportProjectClosurePersistence.getProjectReportProjectClosure(reportId)
            .toDataModel()

}
