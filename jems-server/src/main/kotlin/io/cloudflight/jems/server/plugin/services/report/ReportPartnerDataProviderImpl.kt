package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectPartnerReportProcurementData
import io.cloudflight.jems.plugin.contract.models.report.partner.workPlan.ProjectPartnerReportWorkPackageData
import io.cloudflight.jems.plugin.contract.services.report.ReportPartnerDataProvider
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.toModelData
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure.GetProjectPartnerReportExpenditureCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.GetReportExpenditureCoFinancingBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown.GetReportExpenditureLumpSumBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown.GetReportExpenditureUnitCostBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationService
import io.cloudflight.jems.server.project.service.report.partner.procurement.MAX_AMOUNT_OF_PROCUREMENTS
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment.GetProjectPartnerReportProcurementAttachmentService
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial.GetProjectPartnerReportProcurementBeneficialService
import io.cloudflight.jems.server.project.service.report.partner.procurement.fillThisReportFlag
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract.GetProjectPartnerReportProcurementSubcontractService
import io.cloudflight.jems.server.project.service.report.partner.workPlan.ProjectPartnerReportWorkPlanPersistence
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReportPartnerDataProviderImpl(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val serviceIdentification: GetProjectPartnerReportIdentificationService,
    private val reportContributionPersistence: ProjectPartnerReportContributionPersistence,
    private val calculatorExpenditure: GetProjectPartnerReportExpenditureCalculator,
    private val calculatorCoFinancing: GetReportExpenditureCoFinancingBreakdownCalculator,
    private val calculatorCostCategory: GetReportExpenditureCostCategoryCalculatorService,
    private val calculatorInvestment: GetReportExpenditureInvestmentsBreakdownCalculator,
    private val calculatorLumpSum: GetReportExpenditureLumpSumBreakdownCalculator,
    private val calculatorUnitCost: GetReportExpenditureUnitCostBreakdownCalculator,
    private val reportProcurementPersistence: ProjectPartnerReportProcurementPersistence,
    private val serviceProcurementAttachment: GetProjectPartnerReportProcurementAttachmentService,
    private val serviceProcurementBeneficial: GetProjectPartnerReportProcurementBeneficialService,
    private val serviceProcurementSubcontract: GetProjectPartnerReportProcurementSubcontractService,
    private val reportWorkPlanPersistence: ProjectPartnerReportWorkPlanPersistence,
) : ReportPartnerDataProvider {

    @Transactional(readOnly = true)
    override fun get(partnerId: Long, reportId: Long) =
        reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getIdentification(partnerId: Long, reportId: Long) =
        serviceIdentification.getIdentification(partnerId = partnerId, reportId = reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getContribution(partnerId: Long, reportId: Long) =
        reportContributionPersistence.getPartnerReportContribution(partnerId = partnerId, reportId = reportId)
            .toModelData().toDataModel()

    @Transactional(readOnly = true)
    override fun getExpenditureCosts(partnerId: Long, reportId: Long) =
        calculatorExpenditure.getExpenditureCosts(partnerId = partnerId, reportId = reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getCoFinancingOverview(partnerId: Long, reportId: Long) =
        calculatorCoFinancing.get(partnerId = partnerId, reportId = reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getCostCategoryOverview(partnerId: Long, reportId: Long) =
        calculatorCostCategory.getSubmittedOrCalculateCurrent(partnerId = partnerId, reportId = reportId)
            .toDataModel()

    @Transactional(readOnly = true)
    override fun getInvestmentOverview(partnerId: Long, reportId: Long) =
        calculatorInvestment.get(partnerId = partnerId, reportId = reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getLumpSumOverview(partnerId: Long, reportId: Long) =
        calculatorLumpSum.get(partnerId = partnerId, reportId = reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getUnitCostOverview(partnerId: Long, reportId: Long) =
        calculatorUnitCost.get(partnerId = partnerId, reportId = reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getProcurementList(partnerId: Long, reportId: Long): List<ProjectPartnerReportProcurementData> {
        if (!reportPersistence.exists(partnerId, reportId = reportId))
            return emptyList()

        val previousReportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId)
        val pageable = PageRequest.of(0, MAX_AMOUNT_OF_PROCUREMENTS.toInt(), Sort.by(Sort.Order(Sort.Direction.DESC, "id")))

        return reportProcurementPersistence
            .getProcurementsForReportIds(reportIds = previousReportIds.plus(reportId), pageable = pageable).content
            .toDataModelProcurement()
    }

    @Transactional(readOnly = true)
    override fun getProcurementById(
        partnerId: Long,
        reportId: Long,
        procurementId: Long
    ): ProjectPartnerReportProcurementData? {
        if (!reportPersistence.exists(partnerId, reportId = reportId))
            return null

        return reportProcurementPersistence.getById(partnerId, procurementId = procurementId)
            .fillThisReportFlag(currentReportId = reportId).toDataModel()
    }

    @Transactional(readOnly = true)
    override fun getProcurementAttachment(partnerId: Long, reportId: Long, procurementId: Long) =
        serviceProcurementAttachment.getAttachment(partnerId, reportId = reportId, procurementId = procurementId)
            .toDataModelFile()

    @Transactional(readOnly = true)
    override fun getProcurementBeneficialOwner(partnerId: Long, reportId: Long, procurementId: Long) =
        serviceProcurementBeneficial.getBeneficialOwner(partnerId, reportId = reportId, procurementId = procurementId)
            .toDataModelBeneficial()

    @Transactional(readOnly = true)
    override fun getProcurementSubcontract(partnerId: Long, reportId: Long, procurementId: Long) =
        serviceProcurementSubcontract.getSubcontract(partnerId, reportId = reportId, procurementId = procurementId)
            .toDataModelSubcontract()

    @Transactional(readOnly = true)
    override fun getWorkPlan(partnerId: Long, reportId: Long): List<ProjectPartnerReportWorkPackageData> =
        reportWorkPlanPersistence.getPartnerReportWorkPlanById(partnerId = partnerId, reportId = reportId)
            .toDataModelWorkPlan()

}
