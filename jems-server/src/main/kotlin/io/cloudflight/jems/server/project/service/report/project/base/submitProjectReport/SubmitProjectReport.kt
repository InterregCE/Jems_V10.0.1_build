package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryCurrentlyReported
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.runProjectReportPreSubmissionCheck.RunProjectReportPreSubmissionCheckService
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.plus
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown.plusSpf
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportSubmitted
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime

@Service
class SubmitProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val preSubmissionCheckService: RunProjectReportPreSubmissionCheckService,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val reportIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence,
    private val reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence,
    private val reportExpenditureUnitCostPersistence: ProjectPartnerReportUnitCostPersistence,
    private val reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence,
    private val reportExpenditureInvestmentPersistence: ProjectPartnerReportInvestmentPersistence,
    private val reportCertificateInvestmentPersistence: ProjectReportCertificateInvestmentPersistence,
    private val reportWorkPlanPersistence: ProjectReportWorkPlanPersistence,
    private val reportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence,
    private val reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence,
    private val projectReportProjectClosurePersistence: ProjectReportProjectClosurePersistence,
    private val projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : SubmitProjectReportInteractor {

    companion object {
        private val emptySpf = ReportCertificateCoFinancingColumn(
            funds = emptyMap(),
            partnerContribution = BigDecimal.ZERO,
            publicContribution = BigDecimal.ZERO,
            automaticPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            sum = BigDecimal.ZERO,
        )
    }

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(SubmitProjectReportException::class)
    override fun submit(reportId: Long): ProjectReportStatus {
        val report = reportPersistence.getReportByIdUnSecured(reportId)
        val projectId = report.projectId
        validateReportIsStillOpen(report)

        if (!preSubmissionCheckService.preCheck(projectId, reportId = reportId).isSubmissionAllowed)
            throw SubmissionNotAllowed()

        val certificates = if (report.type == ContractingDeadlineType.Content) emptyList() else
            reportCertificatePersistence.listCertificatesOfProjectReport(reportId)

        if (report.status.isOpenForNumbersChanges()) {
            val certificateIds = certificates.mapTo(HashSet()) { it.id }
            val spfContributionCurrentValues = if (report.type == ContractingDeadlineType.Content) emptySpf else
                reportSpfClaimPersistence.getCurrentSpfContribution(reportId)

            saveCurrentSpendingProfile(reportId)
            saveCurrentCoFinancing(certificateIds, spf = spfContributionCurrentValues, projectId, reportId)
            saveCurrentCostCategories(certificateIds, spfSum = spfContributionCurrentValues.sum, projectId, reportId)
            saveCurrentUnitCosts(certificateIds, projectId, reportId)
            saveCurrentLumpSums(certificateIds, projectId, reportId)
            saveCurrentInvestments(certificateIds, projectId, reportId)

            deleteDataBasedOnContractingDeadlineType(report)
            deleteClosureDataIfReportNotFinal(report)

            reInitiateExpendituresIfVerificationReOpened(report)
        }

        val reportSubmitted = if (report.status.isOpenInitially())
            reportPersistence.submitReportInitially(
                projectId = projectId,
                reportId = reportId,
                submissionTime = ZonedDateTime.now(),
            )
        else
            reportPersistence.reSubmitReport(
                projectId = projectId,
                reportId = reportId,
                newStatus = report.status.submitStatus(hasVerificationStartedBefore = report.hasVerificationStartedBefore()),
                submissionTime = ZonedDateTime.now(),
            )

        return reportSubmitted.also { projectReportSummary ->
            auditPublisher.publishEvent(ProjectReportStatusChanged(this, projectReportSummary, report.status))
            auditPublisher.publishEvent(
                projectReportSubmitted(
                    context = this,
                    projectId = projectId,
                    report = projectReportSummary,
                    certificates = certificates,
                )
            )
        }.status
    }

    private fun saveCurrentSpendingProfile(reportId: Long) {
        val currentSpendingProfile = reportIdentificationPersistence.getSpendingProfileCurrentValues(reportId)
        reportIdentificationPersistence.updateSpendingProfile(reportId, currentValuesByPartnerId = currentSpendingProfile)
    }

    private fun validateReportIsStillOpen(report: ProjectReportModel) {
        if (report.status.isClosed())
            throw ProjectReportAlreadyClosed()
    }

    private fun saveCurrentCoFinancing(certificateIds: Set<Long>, spf: ReportCertificateCoFinancingColumn, projectId: Long, reportId: Long) {
        val certificateCurrentValues = reportExpenditureCoFinancingPersistence.getCoFinancingTotalEligible(certificateIds)
        val currentValues = certificateCurrentValues.plus(spf)

        reportCertificateCoFinancingPersistence.updateCurrentlyReportedValues(
            projectId = projectId,
            reportId = reportId,
            currentlyReported = currentValues,
        )
    }

    private fun saveCurrentCostCategories(certificateIds: Set<Long>, spfSum: BigDecimal, projectId: Long, reportId: Long) {
        val certificateCurrentValues = reportExpenditureCostCategoryPersistence.getCostCategoriesTotalEligible(certificateIds)
        val currentValues = certificateCurrentValues.plusSpf(spfSum)

        reportCertificateCostCategoryPersistence.updateCurrentlyReportedValues(
            projectId = projectId,
            reportId = reportId,
            currentlyReported = CertificateCostCategoryCurrentlyReported(
                currentlyReported = currentValues
            ),
        )
    }

    private fun saveCurrentLumpSums(certificateIds: Set<Long>, projectId: Long, reportId: Long) {
        val currentValues = reportLumpSumPersistence.getLumpSumCumulativeAfterControl(certificateIds)

        reportCertificateLumpSumPersistence.updateCurrentlyReportedValues(
            projectId = projectId,
            reportId = reportId,
            currentValues = currentValues
        )
    }

    private fun saveCurrentUnitCosts(certificateIds: Set<Long>, projectId: Long, reportId: Long) {
        val currentValues = reportExpenditureUnitCostPersistence.getUnitCostCumulativeAfterControl(certificateIds)

        reportCertificateUnitCostPersistence.updateCurrentlyReportedValues(
            projectId = projectId,
            reportId = reportId,
            currentValues = currentValues,
        )
    }

    private fun saveCurrentInvestments(certificateIds: Set<Long>, projectId: Long, reportId: Long) {
        val currentValues = reportExpenditureInvestmentPersistence.getInvestmentsCumulativeAfterControl(certificateIds)

        reportCertificateInvestmentPersistence.updateCurrentlyReportedValues(
            projectId = projectId,
            reportId = reportId,
            currentValues = currentValues,
        )
    }

    private fun deleteDataBasedOnContractingDeadlineType(report: ProjectReportModel) =
        when (report.type!!) {
            ContractingDeadlineType.Finance -> {
                deleteContentOnlyData(report.id)
            }

            ContractingDeadlineType.Content -> {
                deleteFinanceOnlyData(report.id)
            }

            ContractingDeadlineType.Both -> {
                // intentionally left empty
            }
        }

    private fun deleteClosureDataIfReportNotFinal(report: ProjectReportModel) {
        if (!report.finalReport!!)
            projectReportProjectClosurePersistence.deleteProjectReportProjectClosure(report.id)
    }

    private fun ProjectReportModel.hasVerificationStartedBefore() = this.lastVerificationReOpening != null

    private fun deleteContentOnlyData(reportId: Long) {
        reportResultPrinciplePersistence.deleteProjectResultPrinciplesIfExist(reportId)
        reportWorkPlanPersistence.deleteWorkPlan(reportId)
    }

    private fun deleteFinanceOnlyData(reportId: Long) {
        reportCertificatePersistence.deselectCertificatesOfProjectReport(reportId)
        reportSpfClaimPersistence.resetSpfContributionClaims(reportId)
    }

    private fun reInitiateExpendituresIfVerificationReOpened(report: ProjectReportModel) {
        if (report.status == ProjectReportStatus.VerificationReOpenedLast)
            projectReportExpenditureVerificationPersistence.reInitiateVerificationForProjectReport(projectReportId = report.id)
    }

}
