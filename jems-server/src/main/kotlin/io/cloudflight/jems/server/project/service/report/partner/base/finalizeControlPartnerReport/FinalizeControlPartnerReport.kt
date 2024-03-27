package io.cloudflight.jems.server.project.service.report.partner.base.finalizeControlPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCurrentValuesWrapper
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.extractOverview
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.getParkedAndEligibleAfterControl
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.onlyParkedOnes
import io.cloudflight.jems.server.project.service.report.partner.control.overview.runControlPartnerReportPreSubmissionCheck.RunControlPartnerReportPreSubmissionCheckService
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.generateCoFinCalculationInputData
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.getCurrentFrom
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.toColumn
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.getAfterControlForInvestments
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown.getAfterControlForLumpSums
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown.getAfterControlForUnitCosts
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportControlFinalized
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@Service
class FinalizeControlPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val preSubmissionCheckService: RunControlPartnerReportPreSubmissionCheckService,
    private val partnerPersistence: PartnerPersistence,
    private val reportControlExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportContributionPersistence: ProjectPartnerReportContributionPersistence,
    private val reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence,
    private val reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence,
    private val reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence,
    private val controlOverviewPersistence: ProjectPartnerReportControlOverviewPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val controlInstitutionPersistence: ControllerInstitutionPersistence,
    private val reportDesignatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence,
) : FinalizeControlPartnerReportInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(FinalizeControlPartnerReportException::class)
    override fun finalizeControl(partnerId: Long, reportId: Long): ReportStatus {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        validateReportIsInControl(report)

        if (!preSubmissionCheckService.preCheck(partnerId, reportId = reportId).isSubmissionAllowed)
            throw SubmissionNotAllowed()

        val expenditures = reportControlExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(partnerId, reportId = reportId)
        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId, reportId = reportId)
        val parkedAndEligibleAfterControl = getParkedAndEligibleAfterControl(expenditures, costCategories)

        val institution = controlInstitutionPersistence.getControllerInstitutions(setOf(partnerId)).values.first()

        saveAfterControlCostCategories(parkedAndEligibleAfterControl, partnerId = partnerId, reportId) // table 2 breakdown cost-category
        saveAfterControlCoFinancing( // table 1 summary
            afterControlExpenditureCurrent =  parkedAndEligibleAfterControl.currentlyReported.sum,
            afterControlExpenditureParked =  parkedAndEligibleAfterControl.currentlyReportedParked.sum,
            totalEligibleBudget = costCategories.totalBudgetWithoutSpf(),
            report = report, partnerId = partnerId,
        )
        saveAfterControlLumpSums(expenditures.getAfterControlForLumpSums(), partnerId = partnerId, reportId) // table 3
        saveAfterControlUnitCosts(expenditures.getAfterControlForUnitCosts(), partnerId = partnerId, reportId) // table 4
        saveAfterControlInvestments(expenditures.getAfterControlForInvestments(), partnerId = partnerId, reportId) // table 5
        saveInstitutionName(partnerId, reportId, institution.name)
        saveControlEndDate(partnerId, reportId)

        return reportPersistence.finalizeControlOnReportById(
            partnerId = partnerId,
            reportId = reportId,
            controlEnd = ZonedDateTime.now(),
        ).also {
            val projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, it.version)
            auditPublisher.publishEvent(PartnerReportStatusChanged(this, projectId, it, report.status))
            auditPublisher.publishEvent(
                partnerReportControlFinalized(context = this, projectId = projectId, report = it, parked = expenditures.onlyParkedOnes())
            )
        }.status
    }

    private fun validateReportIsInControl(report: ProjectPartnerReport) {
        if (report.status.controlNotFullyOpen())
            throw ReportNotInControl()
    }

    private fun saveAfterControlCostCategories(
        afterControlCostCategoriesWithParked: BudgetCostsCurrentValuesWrapper,
        partnerId: Long,
        reportId: Long
    ) {
        reportExpenditureCostCategoryPersistence.updateAfterControlValues(
            partnerId = partnerId,
            reportId = reportId,
            afterControlWithParked = afterControlCostCategoriesWithParked,
        )
    }

    private fun saveAfterControlCoFinancing(
        afterControlExpenditureCurrent: BigDecimal,
        afterControlExpenditureParked: BigDecimal,
        totalEligibleBudget: BigDecimal,
        report: ProjectPartnerReport,
        partnerId: Long,
    ) {
        val contributions = reportContributionPersistence
            .getPartnerReportContribution(partnerId, reportId = report.id).extractOverview()

        reportExpenditureCoFinancingPersistence.updateAfterControlValues(
            partnerId = partnerId,
            reportId = report.id,
            afterControl = ExpenditureCoFinancingCurrent(
                current = getCurrentFrom(
                    contributions.generateCoFinCalculationInputData(
                        totalEligibleBudget = totalEligibleBudget,
                        currentValueToSplit = afterControlExpenditureCurrent,
                        funds = report.identification.coFinancing,
                    )
                ).toColumn(),
                currentParked = getCurrentFrom(
                    contributions.generateCoFinCalculationInputData(
                        totalEligibleBudget = totalEligibleBudget,
                        currentValueToSplit = afterControlExpenditureParked,
                        funds = report.identification.coFinancing,
                    )
                ).toColumn(),
            ),
        )
    }

    private fun saveAfterControlLumpSums(afterControlLumpSums: Map<Long, ExpenditureLumpSumCurrent>, partnerId: Long, reportId: Long) {
        reportLumpSumPersistence.updateAfterControlValues(
            partnerId = partnerId,
            reportId = reportId,
            afterControl = afterControlLumpSums,
        )
    }

    private fun saveAfterControlUnitCosts(afterControlUnitCosts: Map<Long, ExpenditureUnitCostCurrent>, partnerId: Long, reportId: Long) {
        reportUnitCostPersistence.updateAfterControlValues(
            partnerId = partnerId,
            reportId = reportId,
            afterControl = afterControlUnitCosts
        )
    }

    private fun saveAfterControlInvestments(afterControlInvestments: Map<Long, ExpenditureInvestmentCurrent>, partnerId: Long, reportId: Long) {
        reportInvestmentPersistence.updateAfterControlValues(
            partnerId = partnerId,
            reportId = reportId,
            afterControl = afterControlInvestments,
        )
    }

    private fun saveInstitutionName(partnerId: Long, reportId: Long, institutionName: String) {
        reportDesignatedControllerPersistence.updateWithInstitutionName(partnerId, reportId, institutionName)
    }

    private fun saveControlEndDate(partnerId: Long, reportId: Long) {
        controlOverviewPersistence.updatePartnerControlReportOverviewEndDate(partnerId, reportId, LocalDate.now())
    }

}
